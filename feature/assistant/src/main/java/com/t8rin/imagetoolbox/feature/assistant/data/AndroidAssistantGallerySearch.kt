/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package com.t8rin.imagetoolbox.feature.assistant.data

import android.content.Context
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.resource.ResourceManager
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.domain.SettingsProvider
import com.t8rin.imagetoolbox.core.settings.presentation.model.Setting
import com.t8rin.imagetoolbox.core.settings.presentation.model.key
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.utils.makeLog
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantAnswer
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantGalleryPerson
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantGalleryPhoto
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantGalleryCatalog
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantGalleryQuery
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantGalleryResult
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantGalleryResult.Companion.PREVIEW_COUNT
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantGallerySearch
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantGalleryState
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantSetting
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantTool
import com.t8rin.imagetoolbox.feature.assistant.domain.filterGalleryMedia
import com.t8rin.imagetoolbox.feature.assistant.domain.gallerySearchBlocker
import com.t8rin.imagetoolbox.feature.media_picker.data.hasSemanticSearchPermission
import com.t8rin.imagetoolbox.feature.media_picker.domain.MediaRetriever
import com.t8rin.imagetoolbox.feature.media_picker.domain.SemanticSearchRepository
import com.t8rin.imagetoolbox.feature.media_picker.domain.SemanticSearchScheduler
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.AllowedMedia
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Media
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.SemanticSearchCategory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidAssistantGallerySearch @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settings: SettingsProvider,
    private val mediaRetriever: MediaRetriever,
    private val semantic: SemanticSearchRepository,
    private val scheduler: SemanticSearchScheduler,
    private val resources: ResourceManager,
    private val dispatchers: DispatchersHolder
) : AssistantGallerySearch {
    private data class Session(
        val id: Long,
        val query: AssistantGalleryQuery,
        val source: Set<String>? = null,
        val chosenPeople: Map<String, Long> = emptyMap(),
        val matches: List<String>? = null,
        val offset: Int = 0,
        val ambiguousName: String? = null,
        val choices: Set<Long> = emptySet()
    )

    private var session: Session? = null

    override fun currentQuery(): AssistantGalleryQuery? = session?.query

    override fun isCurrent(sessionId: Long): Boolean = session?.id == sessionId

    override fun clear() {
        session = null
    }

    override fun restore(results: List<AssistantGalleryResult>) {
        if (session != null) return
        val last = results.lastOrNull() ?: return
        val first = results.lastOrNull {
            it.sessionId == last.sessionId && it.photos.size == it.total && (it.state.complete || it.total > 0)
        } ?: last
        val matches = first.photos.takeIf {
            (first.state.complete || first.total > 0) && it.size == first.total
        }?.map { it.uri }
        session = Session(
            id = last.sessionId,
            query = last.query,
            source = last.state.sourceUris?.toSet(),
            chosenPeople = last.state.chosenPeople,
            ambiguousName = last.state.ambiguousName ?: last.query.people.firstOrNull { name ->
                last.people.any { it.name.contains(name, ignoreCase = true) }
            },
            choices = last.state.choices.ifEmpty { last.people.map { it.id }.toSet() },
            matches = matches,
            offset = (last.total - last.photos.size + minOf(
                PREVIEW_COUNT,
                last.photos.size
            )).coerceAtLeast(0)
        )
    }

    override suspend fun catalog(): AssistantGalleryCatalog =
        withContext(dispatchers.ioDispatcher) {
            if (!context.hasSemanticSearchPermission()) return@withContext AssistantGalleryCatalog()
            val albums =
                mediaRetriever.getAlbumsWithType(AllowedMedia.Photos(null)).first().getOrThrow()
                    .map { it.label }
            val names = visiblePeople(media()).map { (person, _) -> person.name }.distinct()
            AssistantGalleryCatalog(names, albums)
    }

    override suspend fun search(
        query: AssistantGalleryQuery,
        mode: AssistantGallerySearch.Mode,
        sessionId: Long
    ): AssistantAnswer {
        val previous = if (mode != AssistantGallerySearch.Mode.New) {
            session ?: return AssistantAnswer(text(R.string.assistant_gallery_no_search))
        } else null
        session = Session(
            id = sessionId,
            query = query.normalized(),
            source = if (mode == AssistantGallerySearch.Mode.Narrow) previous?.matches?.toSet()
                ?: previous?.source else null,
            chosenPeople = previous?.chosenPeople.orEmpty()
        )
        return findPhotos()
    }

    override suspend fun retry(sessionId: Long): AssistantAnswer {
        if (!isCurrent(sessionId)) return AssistantAnswer(text(R.string.assistant_gallery_no_search))
        session = session!!.copy(matches = null, offset = 0)
        return findPhotos()
    }

    override suspend fun choosePerson(sessionId: Long, personId: Long): AssistantAnswer {
        val current = session?.takeIf { it.id == sessionId }
            ?: return AssistantAnswer(text(R.string.assistant_gallery_no_search))
        val name = current.ambiguousName ?: return retry(sessionId)
        require(personId in current.choices)
        session = current.copy(chosenPeople = current.chosenPeople + (name to personId))
        return findPhotos()
    }

    override suspend fun more(sessionId: Long): AssistantAnswer {
        if (!isCurrent(sessionId)) return AssistantAnswer(text(R.string.assistant_gallery_no_search))
        return findPhotos(nextPage = true)
    }

    private suspend fun media(): List<Media> =
        mediaRetriever.getMediaByType(AllowedMedia.Photos(null))
            .first().getOrThrow().filter { it.mimeType.startsWith("image/") }.distinctBy(Media::uri)

    private suspend fun visiblePeople(media: List<Media>) = semantic.people(media).map {
        (it.category as SemanticSearchCategory.Person) to it.media
    }.filter { (person, photos) ->
        !person.hidden && person.name.isNotBlank() && photos.isNotEmpty()
    }

    private suspend fun findPhotos(nextPage: Boolean = false): AssistantAnswer =
        withContext(dispatchers.ioDispatcher) {
            val current = checkNotNull(session)
            try {
                val query = current.query
                val needsAi = query.semantic != null || query.people.isNotEmpty()
                val settings = settings.getSettingsState()
                val snapshot = semantic.state.value
                gallerySearchBlocker(
                    query,
                    settings.isSemanticSearchEnabled,
                    settings.semanticSearchSettings.groupPeople,
                    snapshot
                )
                    ?.takeIf { it != R.string.assistant_gallery_models_missing }
                    ?.let { return@withContext blocked(current, it) }
                if (needsAi && (!snapshot.modelsReady || query.people.isNotEmpty() && !snapshot.faceModelsReady || snapshot.indexedImages == 0)) {
                    semantic.refresh()
                }
                gallerySearchBlocker(
                    query = query,
                    enabled = settings.isSemanticSearchEnabled,
                    groupPeople = settings.semanticSearchSettings.groupPeople,
                    state = semantic.state.value
                )?.let { blocker ->
                    return@withContext blocked(current, blocker)
                }
                if (!context.hasSemanticSearchPermission()) {
                    return@withContext blocked(current, R.string.assistant_gallery_permission)
                }
                val available = media()
                if (needsAi && available.isNotEmpty() && semantic.state.value.indexedImages == 0) {
                    return@withContext blocked(current, R.string.assistant_gallery_index_pending)
                }
                var candidates = filterGalleryMedia(available, query, current.source)
                val groups = if (query.people.isNotEmpty()) {
                    visiblePeople(available)
                } else emptyList()
                for (name in query.people) {
                    val exact = groups.filter { (person, _) -> person.name.equals(name, true) }
                    val matching = exact.ifEmpty {
                        groups.filter { (person, _) -> person.name.contains(name, true) }
                    }
                    val selected = current.chosenPeople[name]
                    val resolved = if (selected != null) {
                        matching.find { (person, _) -> person.personId == selected }
                    } else matching.singleOrNull()
                    if (resolved == null) {
                        if (matching.size > 1 && selected == null) {
                            val choices = matching.map { (person, _) ->
                                AssistantGalleryPerson(
                                    person.personId,
                                    person.name,
                                    person.thumbnail
                                )
                            }
                            val selecting = current.copy(
                                ambiguousName = name,
                                choices = choices.map { it.id }.toSet()
                            )
                            session = selecting
                            return@withContext AssistantAnswer(
                                text = resources.getString(
                                    R.string.assistant_gallery_choose_person,
                                    name
                                ),
                                gallery = result(selecting).copy(people = choices)
                            )
                        }
                        val state = semantic.state.value
                        val reason = when {
                            state.error != null -> R.string.assistant_gallery_search_error
                            state.isQueued || state.isIndexing || state.isFacesIndexing -> R.string.assistant_gallery_index_pending
                            else -> R.string.assistant_gallery_person_missing
                        }
                        return@withContext blocked(current, reason)
                    }
                    val uris = resolved.second.mapTo(hashSetOf(), Media::uri)
                    candidates = candidates.filter { it.uri in uris }
                }
                val matches = if (nextPage && current.matches != null) {
                    val byUri = candidates.associateBy(Media::uri)
                    current.matches.mapNotNull(byUri::get)
                } else if (query.semantic != null) {
                    semantic.search(
                        query.semantic,
                        candidates,
                        settings.semanticSearchSettings.textThreshold
                    )
                } else candidates.sortedByDescending {
                    it.takenTimestamp?.takeIf { date -> date > 0 } ?: (it.timestamp * 1000)
                }
                val latest = this@AndroidAssistantGallerySearch.settings.getSettingsState()
                gallerySearchBlocker(
                    query = query,
                    enabled = latest.isSemanticSearchEnabled,
                    groupPeople = latest.semanticSearchSettings.groupPeople,
                    state = semantic.state.value
                )?.let {
                    return@withContext blocked(current, it)
                }
                if (!context.hasSemanticSearchPermission()) {
                    return@withContext blocked(current, R.string.assistant_gallery_permission)
                }
                val start = if (nextPage) current.offset.coerceAtMost(matches.size) else 0
                val end = (start + PREVIEW_COUNT).coerceAtMost(matches.size)
                val updated = current.copy(
                    matches = matches.map(Media::uri),
                    offset = end,
                    ambiguousName = null,
                    choices = emptySet()
                )
                session = updated
                val summary = if (matches.isEmpty()) text(R.string.assistant_gallery_empty)
                else resources.getString(R.string.assistant_gallery_found, matches.size)
                AssistantAnswer(
                    text = summary,
                    gallery = result(updated).copy(
                        photos = matches.subList(start, matches.size)
                            .map { AssistantGalleryPhoto(it.uri, it.label) },
                        total = matches.size
                    )
                )
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: SecurityException) {
                blocked(current, R.string.assistant_gallery_permission)
            } catch (failure: Exception) {
                failure.makeLog()
                blocked(current, R.string.assistant_gallery_search_error)
            }
        }

    private fun result(current: Session) = AssistantGalleryResult(
        sessionId = current.id,
        query = current.query,
        state = AssistantGalleryState(
            sourceUris = current.source?.toList(),
            chosenPeople = current.chosenPeople,
            ambiguousName = current.ambiguousName,
            choices = current.choices,
            complete = current.matches != null
        )
    )

    private fun blocked(current: Session, reason: Int): AssistantAnswer {
        if (reason == R.string.assistant_gallery_index_pending) scheduler.refresh()
        val disabled = reason == R.string.assistant_gallery_disabled ||
                reason == R.string.assistant_gallery_disabled_people
        return AssistantAnswer(
            text = text(reason),
            gallery = result(current),
            settings = if (disabled) listOf(
                AssistantSetting(
                    key = Setting.SemanticSearch.key(),
                    title = text(Setting.SemanticSearch.title),
                    subtitle = text(R.string.gallery_search_auto_sub)
                )
            ) else emptyList(),
            tools = if (disabled) emptyList() else listOf(
                AssistantTool(
                    key = Screen.Gallery.simpleName,
                    title = text(R.string.assistant_gallery_open),
                    subtitle = text(R.string.custom_gallery_sub)
                )
            )
        )
    }

    private fun text(id: Int) = resources.getString(id)
}
