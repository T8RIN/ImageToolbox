# Contributing Guidelines

This documentation contains set of guidelines to help you during the contribution process.

# Submitting Contributions👨🏻‍💻
Below you will find the process and workflow used to review and merge your changes.
## 🌟 : Choose an issue/ Create an issue

- Look for the existing issue or create your own issue.
- Comment on the respective issue you would like to work before creating a Pull Request.
- Wait for the issue to be assigned to you after which you can start working on it.

## 🌟 : Fork the repository

- Fork this repository "ImageToolbox" by clicking on the "Fork" button. This will create a local copy of this respository on your GitHub profile.

## 🌟 : Clone the forked repository

- Once the repository is forked you need to clone it to your local machine.
- Click on the "Code" button in the repository page and copy the link provided in the dropdown menu.


```bash
git clone https://github.com/<your-username>/<ImageToolbox>  
```

- Keep a reference to the original project in `upstream` remote.

```bash  
cd <repo-name>  
git remote add upstream https://github.com/<upstream-owner>/<ImageToolbox>
git remote -v # To the check the remotes for this repository 
```  

- If the project is forked already, update the copy before working.

```bash
git remote update
git checkout <branch-name>
git rebase upstream/<branch-name>
``` 

## 🌟 : Create a new branch

- Always create a new branch and name it accordingly so as to identify the issue you are addressing.

```bash
# It will create a new branch with name branch_name and switch to that branch 
git checkout -b branch_name
```
## 🌟 : Work on the issue assigned

- Work on the issue(s) assigned to you, make the necessary changes in the files/folders needed.
- After making the changes add them to the branch you've created.

```bash  
# To add all new files to branch Branch_Name  
git add .  

# To add only a few files to Branch_Name
git add <file name>
```
## 🌟 : Commit the changes

- Add your commits.
- Along with the commit give a descriptive message that reflects your changes.

```bash
git commit -m "message"  
```
- Note : A Pull Request should always have only one commit. 

## 🌟 : Push the changes

- Push the committed changes in your branch to your remote repository.

```bash  
git push origin branch_name
```
## 🌟 : Create a Pull Request

- Go to your repository in the browser and click on compare and pull request.
- Add a title and description to your pull request that best describes your contribution.
- After which the pull request will be reviewed and the maintainer will provide the reviews required for the changes.

If no changes are needed, this means that your Pull Request has been reviewed and will be merged to the original code base by the maintainer.

## Adding a filter

A standard filter needs a domain contract and one implementation file. KSP generates the parameter
model, UI filter, value factories, `copy` extensions, catalog entries and mappings. The
transformation bases provide cache keys from the filter's value.

1. Add `Filter.Name` and its nested `Value` interface
   to [Filter.kt](core/filters/src/main/java/com/t8rin/imagetoolbox/core/filters/domain/model/Filter.kt).
   Declare named, immutable properties instead of wrapping a `Pair` or another parameter class. For
   example, the existing brightness filter declares:

   ```kotlin
   interface Brightness : Filter<Brightness.Value> {
       interface Value : ParameterValue {
           val bias: Float
       }
   }
   ```

2. Add `NameFilter.kt` in `feature/filters/data/model`. Implement the domain interface and the
   appropriate transformation base. The companion defines the title, help, defaults and controls.
   This is the
   corresponding [BrightnessFilter](feature/filters/src/main/java/com/t8rin/imagetoolbox/feature/filters/data/model/BrightnessFilter.kt):

   ```kotlin
   import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Brightness.Value

   @FilterDefinition(group = FilterGroups.LIGHT)
   internal class BrightnessFilter(
       override val value: Value
   ) : FilterTransformation, Filter.Brightness {
       override suspend fun transform(input: Bitmap, size: IntegerSize): Bitmap =
           Aire.brightness(bitmap = input, bias = value.bias)

       companion object : Filter.Schema.Structured<Value> {
           override val info = FilterInfo(R.string.brightness, R.string.filter_help_brightness)

           override val parameters = structuredParameters {
               Value::bias.float(default = 0.5f, range = -1f..1f)
           }
       }
   }
   ```

Add the corresponding title/help resources using the existing resource format. Bind each `Value`
property once in `parameters`; declaration order determines the control order. Use the typed
property helpers: `.float`, `.int`, `.seed`, `.choice`, `.boolean`, `.toggle`, `.color`, `.text`,
`.array`, `.enum`, `.palette`, `.image`, `.file`, `.font`, `.point` and `.curves`. Enum labels
return string resource IDs; technical names that should not be translated use
`translatable="false"` resources. Source implementations depend on the domain `Value`, so no
generated class is needed to describe a new filter. Schemas contain data only; Compose and editor
implementations belong in `presentation`.

Templates store fields by property name, independently of the control order. New fields use their
schema defaults when absent from a template. Renaming or removing a released field is a storage
change and needs an explicit migration. The frozen legacy contracts describe only the old, released
format; do not add new filters or intermediate refactor formats to that table.

For a filter without parameters, declare `Value : EmptyValue` and have the companion implement
`Filter.Schema.Empty<Value>`;
see [BlackAndWhiteFilter](feature/filters/src/main/java/com/t8rin/imagetoolbox/feature/filters/data/model/BlackAndWhiteFilter.kt).

Use `Filter.Schema.Custom<Value>` only when the UI needs a dedicated editor, such as interactive
curves or grouped NTSC settings. Keep its fields and defaults in the same companion, and dispatch
the editor by `Filter.Name` in `FilterItemContent`. Editors work with `Value` directly: import
`com.t8rin.imagetoolbox.core.filters.domain.model.copy` for generated `value.copy(...)`, and
`com.t8rin.imagetoolbox.core.filters.presentation.model.parameterInfo` for typed metadata such as
`filter.parameterInfo(Filter.Ascii.Value::fontSize)`. Custom values that must be saved need a typed
field codec. For a single special control, keep `Filter.Schema.Structured` and bind the typed
property to an editor in `FilterFieldItem.kt`; the remaining controls are generated normally. Custom
fields such as `Value::maskFile.editor { FileModel("") }` fix the value type from the property
before checking their defaults and codecs. To reuse a descriptor, bind it with
`Value::property.parameter(descriptor)`. Missing field bindings and editors fail with a descriptive
error when the filter is created. For custom values without a template codec, supply a `cache`
mapping to supported immutable values when the default cache encoder does not support their type.

Build to run KSP; do not add a handwritten `NameParams`, `UiNameFilter`, mapping entry or
`cacheKey`. When changing an existing filter, preserve released template payloads through the
serialization layer and verify restoration with the filter serialization tests. Intermediate
refactor formats do not need compatibility aliases. Sharing and QR export use compressed links when
compression makes them smaller; the importer also accepts the original plain links.

Happy Hacking!
