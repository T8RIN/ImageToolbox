package io.github.alexzhirkevich.qrose

object QrData

fun QrData.text(text: String): String = text

fun QrData.phone(phoneNumber: String): String = "TEL:$phoneNumber"

fun QrData.email(
    email: String,
    copyTo: String? = null,
    subject: String? = null,
    body: String? = null
): String = buildString {
    append("mailto:$email")

    if (listOf(copyTo, subject, body).any { it.isNullOrEmpty().not() }) {
        append("?")
    }
    val querries = buildList<String> {
        if (copyTo.isNullOrEmpty().not()) {
            add("cc=$copyTo")
        }
        if (subject.isNullOrEmpty().not()) {
            add("subject=${escape(subject!!)}")
        }
        if (body.isNullOrEmpty().not()) {
            add("body=${escape(body!!)}")
        }
    }
    append(querries.joinToString(separator = "&"))
}

fun QrData.sms(
    phoneNumber: String,
    subject: String,
    isMMS: Boolean
): String = "${if (isMMS) "MMS" else "SMS"}:" +
        "$phoneNumber${if (subject.isNotEmpty()) ":$subject" else ""}"


fun QrData.meCard(
    name: String? = null,
    address: String? = null,
    phoneNumber: String? = null,
    email: String? = null,
): String = buildString {
    append("MECARD:")
    if (name != null)
        append("N:$name;")

    if (address != null)
        append("ADR:$address;")

    if (phoneNumber != null)
        append("TEL:$phoneNumber;")

    if (email != null)
        append("EMAIL:$email;")

    append(";")
}

fun QrData.vCard(
    name: String? = null,
    company: String? = null,
    title: String? = null,
    phoneNumber: String? = null,
    email: String? = null,
    address: String? = null,
    website: String? = null,
    note: String? = null,
): String = buildString {
    append("BEGIN:VCARD\n")
    append("VERSION:3.0\n")
    if (name != null)
        append("N:$name\n")

    if (company != null)
        append("ORG:$company\n")

    if (title != null)
        append("TITLE$title\n")

    if (phoneNumber != null)
        append("TEL:$phoneNumber\n")

    if (website != null)
        append("URL:$website\n")

    if (email != null)
        append("EMAIL:$email\n")

    if (address != null)
        append("ADR:$address\n")

    if (note != null) {
        append("NOTE:$note\n")
    }
    append("END:VCARD")
}

fun QrData.bizCard(
    firstName: String? = null,
    secondName: String? = null,
    job: String? = null,
    company: String? = null,
    address: String? = null,
    phone: String? = null,
    email: String? = null,
): String = buildString {
    append("BIZCARD:")
    if (firstName != null)
        append("N:$firstName;")

    if (secondName != null)
        append("X:$secondName;")

    if (job != null)
        append("T:$job;")

    if (company != null)
        append("C:$company;")

    if (address != null)
        append("A:$address;")

    if (phone != null)
        append("B:$phone;")

    if (email != null)
        append("E:$email;")

    append(";")
}

fun QrData.wifi(
    authentication: String? = null,
    ssid: String? = null,
    psk: String? = null,
    hidden: Boolean = false,
): String = buildString {
    append("WIFI:")
    if (ssid != null)
        append("S:${escape(ssid)};")

    if (authentication != null)
        append("T:${authentication};")

    if (psk != null)
        append("P:${escape(psk)};")

    append("H:$hidden;")
}

fun QrData.enterpriseWifi(
    ssid: String? = null,
    psk: String? = null,
    hidden: Boolean = false,
    user: String? = null,
    eap: String? = null,
    phase: String? = null,
): String = buildString {
    append("WIFI:")
    if (ssid != null)
        append("S:${escape(ssid)};")

    if (user != null)
        append("U:${escape(user)};")

    if (psk != null)
        append("P:${escape(psk)};")

    if (eap != null)
        append("E:${escape(eap)};")

    if (phase != null)
        append("PH:${escape(phase)};")

    append("H:$hidden;")
}


fun QrData.event(
    uid: String? = null,
    stamp: String? = null,
    organizer: String? = null,
    start: String? = null,
    end: String? = null,
    summary: String? = null,
): String = buildString {
    append("BEGIN:VEVENT\n")
    if (uid != null)
        append("UID:$uid\n")
    if (stamp != null)
        append("DTSTAMP:$stamp\n")
    if (organizer != null)
        append("ORGANIZER:$organizer\n")

    if (start != null)
        append("DTSTART:$start\n")

    if (end != null)
        append("DTEND:$end\n")
    if (summary != null)
        append("SUMMARY:$summary\n")

    append("END:VEVENT")
}

fun QrData.location(
    lat: Float,
    lon: Float
): String = "GEO:$lat,$lon"


private fun escape(text: String): String {
    return text.replace("\\", "\\\\")
        .replace(",", "\\,")
        .replace(";", "\\;")
        .replace(".", "\\.")
        .replace("\"", "\\\"")
        .replace("'", "\\'")
}