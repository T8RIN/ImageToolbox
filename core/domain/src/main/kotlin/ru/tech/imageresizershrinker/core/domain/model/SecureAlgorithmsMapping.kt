/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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
@file:Suppress("EnumEntryName", "unused")

package ru.tech.imageresizershrinker.core.domain.model

/**
 * This utility class maps algorithm name to the corresponding oid strings.
 * NOTE: for 100% backward compatibility, the standard name for the enum
 * is determined by existing usage and may be in lowercase/uppercase in
 * order to match existing output.
 */
enum class SecureAlgorithmsMapping {
    // X.500 Attributes 2.5.4.*
    CommonName("2.5.4.3"),
    Surname("2.5.4.4"),
    SerialNumber("2.5.4.5"),
    CountryName("2.5.4.6"),
    LocalityName("2.5.4.7"),
    StateName("2.5.4.8"),
    StreetAddress("2.5.4.9"),
    OrgName("2.5.4.10"),
    OrgUnitName("2.5.4.11"),
    Title("2.5.4.12"),
    GivenName("2.5.4.42"),
    Initials("2.5.4.43"),
    GenerationQualifier("2.5.4.44"),
    DNQualifier("2.5.4.46"),

    // Certificate Extension 2.5.29.*
    SubjectDirectoryAttributes("2.5.29.9"),
    SubjectKeyID("2.5.29.14"),
    KeyUsage("2.5.29.15"),
    PrivateKeyUsage("2.5.29.16"),
    SubjectAlternativeName("2.5.29.17"),
    IssuerAlternativeName("2.5.29.18"),
    BasicConstraints("2.5.29.19"),
    CRLNumber("2.5.29.20"),
    ReasonCode("2.5.29.21"),
    HoldInstructionCode("2.5.29.23"),
    InvalidityDate("2.5.29.24"),
    DeltaCRLIndicator("2.5.29.27"),
    IssuingDistributionPoint("2.5.29.28"),
    CertificateIssuer("2.5.29.29"),
    NameConstraints("2.5.29.30"),
    CRLDistributionPoints("2.5.29.31"),
    CertificatePolicies("2.5.29.32"),
    CE_CERT_POLICIES_ANY("2.5.29.32.0"),
    PolicyMappings("2.5.29.33"),
    AuthorityKeyID("2.5.29.35"),
    PolicyConstraints("2.5.29.36"),
    extendedKeyUsage("2.5.29.37"),
    anyExtendedKeyUsage("2.5.29.37.0"),
    FreshestCRL("2.5.29.46"),
    InhibitAnyPolicy("2.5.29.54"),

    // PKIX 1.3.6.1.5.5.7.
    AuthInfoAccess("1.3.6.1.5.5.7.1.1"),
    SubjectInfoAccess("1.3.6.1.5.5.7.1.11"),

    // key usage purposes - PKIX.3.*
    serverAuth("1.3.6.1.5.5.7.3.1"),
    clientAuth("1.3.6.1.5.5.7.3.2"),
    codeSigning("1.3.6.1.5.5.7.3.3"),
    emailProtection("1.3.6.1.5.5.7.3.4"),
    ipsecEndSystem("1.3.6.1.5.5.7.3.5"),
    ipsecTunnel("1.3.6.1.5.5.7.3.6"),
    ipsecUser("1.3.6.1.5.5.7.3.7"),
    KP_TimeStamping("1.3.6.1.5.5.7.3.8", "timeStamping"),
    OCSPSigning("1.3.6.1.5.5.7.3.9"),

    // access descriptors - PKIX.48.*
    OCSP("1.3.6.1.5.5.7.48.1"),
    OCSPBasicResponse("1.3.6.1.5.5.7.48.1.1"),
    OCSPNonceExt("1.3.6.1.5.5.7.48.1.2"),
    OCSPNoCheck("1.3.6.1.5.5.7.48.1.5"),
    caIssuers("1.3.6.1.5.5.7.48.2"),
    AD_TimeStamping("1.3.6.1.5.5.7.48.3", "timeStamping"),
    caRepository("1.3.6.1.5.5.7.48.5", "caRepository"),

    // NIST --
    // AES 2.16.840.1.101.3.4.1.*
    AES("2.16.840.1.101.3.4.1"),
    AES_128_ECB_NoPadding("2.16.840.1.101.3.4.1.1", "AES_128/ECB/NoPadding"),
    AES_128_CBC_NoPadding("2.16.840.1.101.3.4.1.2", "AES_128/CBC/NoPadding"),
    AES_128_OFB_NoPadding("2.16.840.1.101.3.4.1.3", "AES_128/OFB/NoPadding"),
    AES_128_CFB_NoPadding("2.16.840.1.101.3.4.1.4", "AES_128/CFB/NoPadding"),
    AES_128_KW_NoPadding(
        "2.16.840.1.101.3.4.1.5", "AES_128/KW/NoPadding",
        "AESWrap_128"
    ),
    AES_128_GCM_NoPadding("2.16.840.1.101.3.4.1.6", "AES_128/GCM/NoPadding"),
    AES_128_KWP_NoPadding(
        "2.16.840.1.101.3.4.1.8", "AES_128/KWP/NoPadding",
        "AESWrapPad_128"
    ),

    AES_192_ECB_NoPadding("2.16.840.1.101.3.4.1.21", "AES_192/ECB/NoPadding"),
    AES_192_CBC_NoPadding("2.16.840.1.101.3.4.1.22", "AES_192/CBC/NoPadding"),
    AES_192_OFB_NoPadding("2.16.840.1.101.3.4.1.23", "AES_192/OFB/NoPadding"),
    AES_192_CFB_NoPadding("2.16.840.1.101.3.4.1.24", "AES_192/CFB/NoPadding"),
    AES_192_KW_NoPadding(
        "2.16.840.1.101.3.4.1.25", "AES_192/KW/NoPadding",
        "AESWrap_192"
    ),
    AES_192_GCM_NoPadding("2.16.840.1.101.3.4.1.26", "AES_192/GCM/NoPadding"),
    AES_192_KWP_NoPadding(
        "2.16.840.1.101.3.4.1.28", "AES_192/KWP/NoPadding",
        "AESWrapPad_192"
    ),

    AES_256_ECB_NoPadding("2.16.840.1.101.3.4.1.41", "AES_256/ECB/NoPadding"),
    AES_256_CBC_NoPadding("2.16.840.1.101.3.4.1.42", "AES_256/CBC/NoPadding"),
    AES_256_OFB_NoPadding("2.16.840.1.101.3.4.1.43", "AES_256/OFB/NoPadding"),
    AES_256_CFB_NoPadding("2.16.840.1.101.3.4.1.44", "AES_256/CFB/NoPadding"),
    AES_256_KW_NoPadding(
        "2.16.840.1.101.3.4.1.45", "AES_256/KW/NoPadding",
        "AESWrap_256"
    ),
    AES_256_GCM_NoPadding("2.16.840.1.101.3.4.1.46", "AES_256/GCM/NoPadding"),
    AES_256_KWP_NoPadding(
        "2.16.840.1.101.3.4.1.48", "AES_256/KWP/NoPadding",
        "AESWrapPad_256"
    ),

    // hashAlgs 2.16.840.1.101.3.4.2.*
    SHA_256("2.16.840.1.101.3.4.2.1", "SHA-256", "SHA256"),
    SHA_384("2.16.840.1.101.3.4.2.2", "SHA-384", "SHA384"),
    SHA_512("2.16.840.1.101.3.4.2.3", "SHA-512", "SHA512"),
    SHA_224("2.16.840.1.101.3.4.2.4", "SHA-224", "SHA224"),
    SHA_512_224("2.16.840.1.101.3.4.2.5", "SHA-512/224", "SHA512/224"),
    SHA_512_256("2.16.840.1.101.3.4.2.6", "SHA-512/256", "SHA512/256"),
    SHA3_224("2.16.840.1.101.3.4.2.7", "SHA3-224"),
    SHA3_256("2.16.840.1.101.3.4.2.8", "SHA3-256"),
    SHA3_384("2.16.840.1.101.3.4.2.9", "SHA3-384"),
    SHA3_512("2.16.840.1.101.3.4.2.10", "SHA3-512"),
    SHAKE128("2.16.840.1.101.3.4.2.11"),
    SHAKE256("2.16.840.1.101.3.4.2.12"),
    HmacSHA3_224("2.16.840.1.101.3.4.2.13", "HmacSHA3-224"),
    HmacSHA3_256("2.16.840.1.101.3.4.2.14", "HmacSHA3-256"),
    HmacSHA3_384("2.16.840.1.101.3.4.2.15", "HmacSHA3-384"),
    HmacSHA3_512("2.16.840.1.101.3.4.2.16", "HmacSHA3-512"),
    SHAKE128_LEN("2.16.840.1.101.3.4.2.17", "SHAKE128-LEN"),
    SHAKE256_LEN("2.16.840.1.101.3.4.2.18", "SHAKE256-LEN"),
    SHA512_224("1.0.10118.3.0.55", "SHA-512/224"),
    SHA512_256("2.16.840.1.101.3.4.2.12", "SHA-512/256"),
    SHA384("2.16.840.1.101.3.4.2.11", "SHA-384"),
    SHA512("2.16.840.1.101.3.4.2.10", "SHA-512"),
    DSTU7564_256("1.2.804.2.1.1.1.1.2.2.3", "DSTU-7564-256"),
    DSTU7564_384("1.2.804.2.1.1.1.1.2.2.2", "DSTU-7564-384"),
    DSTU7564_512("1.2.804.2.1.1.1.1.2.2.1", "DSTU-7564-512"),
    SHA224("2.16.840.1.101.3.4.2.9", "SHA-224"),
    SHA256("2.16.840.1.101.3.4.2.8", "SHA-256"),
    SHA1("2.16.840.1.101.3.4.2.7", "SHA-1"),

    // sigAlgs 2.16.840.1.101.3.4.3.*
    SHA224withDSA("2.16.840.1.101.3.4.3.1"),
    SHA256withDSA("2.16.840.1.101.3.4.3.2"),
    SHA384withDSA("2.16.840.1.101.3.4.3.3"),
    SHA512withDSA("2.16.840.1.101.3.4.3.4"),
    SHA3_224withDSA("2.16.840.1.101.3.4.3.5", "SHA3-224withDSA"),
    SHA3_256withDSA("2.16.840.1.101.3.4.3.6", "SHA3-256withDSA"),
    SHA3_384withDSA("2.16.840.1.101.3.4.3.7", "SHA3-384withDSA"),
    SHA3_512withDSA("2.16.840.1.101.3.4.3.8", "SHA3-512withDSA"),
    SHA3_224withECDSA("2.16.840.1.101.3.4.3.9", "SHA3-224withECDSA"),
    SHA3_256withECDSA("2.16.840.1.101.3.4.3.10", "SHA3-256withECDSA"),
    SHA3_384withECDSA("2.16.840.1.101.3.4.3.11", "SHA3-384withECDSA"),
    SHA3_512withECDSA("2.16.840.1.101.3.4.3.12", "SHA3-512withECDSA"),
    SHA3_224withRSA("2.16.840.1.101.3.4.3.13", "SHA3-224withRSA"),
    SHA3_256withRSA("2.16.840.1.101.3.4.3.14", "SHA3-256withRSA"),
    SHA3_384withRSA("2.16.840.1.101.3.4.3.15", "SHA3-384withRSA"),
    SHA3_512withRSA("2.16.840.1.101.3.4.3.16", "SHA3-512withRSA"),
    ML_DSA_44("2.16.840.1.101.3.4.3.17", "ML-DSA-44"),
    ML_DSA_65("2.16.840.1.101.3.4.3.18", "ML-DSA-65"),
    ML_DSA_87("2.16.840.1.101.3.4.3.19", "ML-DSA-87"),

    // kems 2.16.840.1.101.3.4.4.*
    ML_KEM_512("2.16.840.1.101.3.4.4.1", "ML-KEM-512"),
    ML_KEM_768("2.16.840.1.101.3.4.4.2", "ML-KEM-768"),
    ML_KEM_1024("2.16.840.1.101.3.4.4.3", "ML-KEM-1024"),

    // RSASecurity
    // PKCS1 1.2.840.113549.1.1.*
    PKCS1("1.2.840.113549.1.1", "RSA"),
    RSA("1.2.840.113549.1.1.1"),  // RSA encryption

    MD2withRSA("1.2.840.113549.1.1.2"),
    MD5withRSA("1.2.840.113549.1.1.4"),
    SHA1withRSA("1.2.840.113549.1.1.5"),
    OAEP("1.2.840.113549.1.1.7"),
    MGF1("1.2.840.113549.1.1.8"),
    PSpecified("1.2.840.113549.1.1.9"),
    RSASSA_PSS("1.2.840.113549.1.1.10", "RSASSA-PSS", "PSS"),
    SHA256withRSA("1.2.840.113549.1.1.11"),
    SHA384withRSA("1.2.840.113549.1.1.12"),
    SHA512withRSA("1.2.840.113549.1.1.13"),
    SHA224withRSA("1.2.840.113549.1.1.14"),
    SHA512_224withRSA("1.2.840.113549.1.1.15", "SHA512/224withRSA"),
    SHA512_256withRSA("1.2.840.113549.1.1.16", "SHA512/256withRSA"),

    // PKCS3 1.2.840.113549.1.3.*
    DiffieHellman("1.2.840.113549.1.3.1", "DiffieHellman", "DH"),

    // PKCS5 1.2.840.113549.1.5.*
    PBEWithMD5AndDES("1.2.840.113549.1.5.3"),
    PBEWithMD5AndRC2("1.2.840.113549.1.5.6"),
    PBEWithSHA1AndDES("1.2.840.113549.1.5.10"),
    PBEWithSHA1AndRC2("1.2.840.113549.1.5.11"),
    PBKDF2WithHmacSHA1("1.2.840.113549.1.5.12"),
    PBES2("1.2.840.113549.1.5.13"),

    // PKCS7 1.2.840.113549.1.7.*
    PKCS7("1.2.840.113549.1.7"),
    Data("1.2.840.113549.1.7.1"),
    SignedData("1.2.840.113549.1.7.2"),
    JDK_OLD_Data("1.2.840.1113549.1.7.1"),  // extra 1 in 4th component
    JDK_OLD_SignedData("1.2.840.1113549.1.7.2"),
    EnvelopedData("1.2.840.113549.1.7.3"),
    SignedAndEnvelopedData("1.2.840.113549.1.7.4"),
    DigestedData("1.2.840.113549.1.7.5"),
    EncryptedData("1.2.840.113549.1.7.6"),

    // PKCS9 1.2.840.113549.1.9.*
    EmailAddress("1.2.840.113549.1.9.1"),
    UnstructuredName("1.2.840.113549.1.9.2"),
    ContentType("1.2.840.113549.1.9.3"),
    MessageDigest("1.2.840.113549.1.9.4"),
    SigningTime("1.2.840.113549.1.9.5"),
    CounterSignature("1.2.840.113549.1.9.6"),
    ChallengePassword("1.2.840.113549.1.9.7"),
    UnstructuredAddress("1.2.840.113549.1.9.8"),
    ExtendedCertificateAttributes("1.2.840.113549.1.9.9"),
    IssuerAndSerialNumber("1.2.840.113549.1.9.10"),
    ExtensionRequest("1.2.840.113549.1.9.14"),
    SMIMECapability("1.2.840.113549.1.9.15"),
    TimeStampTokenInfo("1.2.840.113549.1.9.16.1.4"),
    SigningCertificate("1.2.840.113549.1.9.16.2.12"),
    SignatureTimestampToken("1.2.840.113549.1.9.16.2.14"),
    HSSLMS("1.2.840.113549.1.9.16.3.17", "HSS/LMS"),
    CHACHA20_POLY1305("1.2.840.113549.1.9.16.3.18", "CHACHA20-POLY1305"),
    FriendlyName("1.2.840.113549.1.9.20"),
    LocalKeyID("1.2.840.113549.1.9.21"),
    CertTypeX509("1.2.840.113549.1.9.22.1"),
    CMSAlgorithmProtection("1.2.840.113549.1.9.52"),

    // PKCS12 1.2.840.113549.1.12.*
    PBEWithSHA1AndRC4_128("1.2.840.113549.1.12.1.1"),
    PBEWithSHA1AndRC4_40("1.2.840.113549.1.12.1.2"),
    PBEWithSHA1AndDESede("1.2.840.113549.1.12.1.3"),
    PBEWithSHA1AndRC2_128("1.2.840.113549.1.12.1.5"),
    PBEWithSHA1AndRC2_40("1.2.840.113549.1.12.1.6"),
    PKCS8ShroudedKeyBag("1.2.840.113549.1.12.10.1.2"),
    CertBag("1.2.840.113549.1.12.10.1.3"),
    SecretBag("1.2.840.113549.1.12.10.1.5"),

    // digestAlgs 1.2.840.113549.2.*
    MD2("1.2.840.113549.2.2"),
    MD5("1.2.840.113549.2.5"),
    HmacSHA1("1.2.840.113549.2.7"),
    HmacSHA224("1.2.840.113549.2.8"),
    HmacSHA256("1.2.840.113549.2.9"),
    HmacSHA384("1.2.840.113549.2.10"),
    HmacSHA512("1.2.840.113549.2.11"),
    HmacSHA512_224("1.2.840.113549.2.12", "HmacSHA512/224"),
    HmacSHA512_256("1.2.840.113549.2.13", "HmacSHA512/256"),

    // encryptionAlgs 1.2.840.113549.3.*
    RC2_CBC_PKCS5Padding("1.2.840.113549.3.2", "RC2/CBC/PKCS5Padding", "RC2"),
    ARCFOUR("1.2.840.113549.3.4", "ARCFOUR", "RC4"),
    DESede_CBC_NoPadding("1.2.840.113549.3.7", "DESede/CBC/NoPadding"),
    RC5_CBC_PKCS5Padding("1.2.840.113549.3.9", "RC5/CBC/PKCS5Padding"),

    // ANSI --
    // X9 1.2.840.10040.4.*
    DSA("1.2.840.10040.4.1"),
    SHA1withDSA("1.2.840.10040.4.3", "SHA1withDSA", "DSS"),

    // X9.62 1.2.840.10045.*
    EC("1.2.840.10045.2.1"),

    c2pnb163v1("1.2.840.10045.3.0.1", "X9.62 c2pnb163v1"),
    c2pnb163v2("1.2.840.10045.3.0.2", "X9.62 c2pnb163v2"),
    c2pnb163v3("1.2.840.10045.3.0.3", "X9.62 c2pnb163v3"),
    c2pnb176w1("1.2.840.10045.3.0.4", "X9.62 c2pnb176w1"),
    c2tnb191v1("1.2.840.10045.3.0.5", "X9.62 c2tnb191v1"),
    c2tnb191v2("1.2.840.10045.3.0.6", "X9.62 c2tnb191v2"),
    c2tnb191v3("1.2.840.10045.3.0.7", "X9.62 c2tnb191v3"),

    c2pnb208w1("1.2.840.10045.3.0.10", "X9.62 c2pnb208w1"),
    c2tnb239v1("1.2.840.10045.3.0.11", "X9.62 c2tnb239v1"),
    c2tnb239v2("1.2.840.10045.3.0.12", "X9.62 c2tnb239v2"),
    c2tnb239v3("1.2.840.10045.3.0.13", "X9.62 c2tnb239v3"),

    c2pnb272w1("1.2.840.10045.3.0.16", "X9.62 c2pnb272w1"),
    c2pnb304w1("1.2.840.10045.3.0.17", "X9.62 c2pnb304w1"),
    c2tnb359v1("1.2.840.10045.3.0.18", "X9.62 c2tnb359v1"),

    c2pnb368w1("1.2.840.10045.3.0.19", "X9.62 c2pnb368w1"),
    c2tnb431r1("1.2.840.10045.3.0.20", "X9.62 c2tnb431r1"),

    secp192r1(
        "1.2.840.10045.3.1.1",
        "secp192r1", "NIST P-192", "X9.62 prime192v1"
    ),
    prime192v2("1.2.840.10045.3.1.2", "X9.62 prime192v2"),
    prime192v3("1.2.840.10045.3.1.3", "X9.62 prime192v3"),
    prime239v1("1.2.840.10045.3.1.4", "X9.62 prime239v1"),
    prime239v2("1.2.840.10045.3.1.5", "X9.62 prime239v2"),
    prime239v3("1.2.840.10045.3.1.6", "X9.62 prime239v3"),
    secp256r1(
        "1.2.840.10045.3.1.7",
        "secp256r1", "NIST P-256", "X9.62 prime256v1"
    ),
    SHA1withECDSA("1.2.840.10045.4.1"),
    SHA224withECDSA("1.2.840.10045.4.3.1"),
    SHA256withECDSA("1.2.840.10045.4.3.2"),
    SHA384withECDSA("1.2.840.10045.4.3.3"),
    SHA512withECDSA("1.2.840.10045.4.3.4"),
    SpecifiedSHA2withECDSA("1.2.840.10045.4.3"),

    // X9.42 1.2.840.10046.2.*
    X942_DH("1.2.840.10046.2.1", "DiffieHellman"),

    // Teletrust 1.3.36.*
    brainpoolP160r1("1.3.36.3.3.2.8.1.1.1"),
    brainpoolP192r1("1.3.36.3.3.2.8.1.1.3"),
    brainpoolP224r1("1.3.36.3.3.2.8.1.1.5"),
    brainpoolP256r1("1.3.36.3.3.2.8.1.1.7"),
    brainpoolP320r1("1.3.36.3.3.2.8.1.1.9"),
    brainpoolP384r1("1.3.36.3.3.2.8.1.1.11"),
    brainpoolP512r1("1.3.36.3.3.2.8.1.1.13"),

    // Certicom 1.3.132.*
    sect163k1("1.3.132.0.1", "sect163k1", "NIST K-163"),
    sect163r1("1.3.132.0.2"),
    sect239k1("1.3.132.0.3"),
    sect113r1("1.3.132.0.4"),
    sect113r2("1.3.132.0.5"),
    secp112r1("1.3.132.0.6"),
    secp112r2("1.3.132.0.7"),
    secp160r1("1.3.132.0.8"),
    secp160k1("1.3.132.0.9"),
    secp256k1("1.3.132.0.10"),
    sect163r2("1.3.132.0.15", "sect163r2", "NIST B-163"),
    sect283k1("1.3.132.0.16", "sect283k1", "NIST K-283"),
    sect283r1("1.3.132.0.17", "sect283r1", "NIST B-283"),

    sect131r1("1.3.132.0.22"),
    sect131r2("1.3.132.0.23"),
    sect193r1("1.3.132.0.24"),
    sect193r2("1.3.132.0.25"),
    sect233k1("1.3.132.0.26", "sect233k1", "NIST K-233"),
    sect233r1("1.3.132.0.27", "sect233r1", "NIST B-233"),
    secp128r1("1.3.132.0.28"),
    secp128r2("1.3.132.0.29"),
    secp160r2("1.3.132.0.30"),
    secp192k1("1.3.132.0.31"),
    secp224k1("1.3.132.0.32"),
    secp224r1("1.3.132.0.33", "secp224r1", "NIST P-224"),
    secp384r1("1.3.132.0.34", "secp384r1", "NIST P-384"),
    secp521r1("1.3.132.0.35", "secp521r1", "NIST P-521"),
    sect409k1("1.3.132.0.36", "sect409k1", "NIST K-409"),
    sect409r1("1.3.132.0.37", "sect409r1", "NIST B-409"),
    sect571k1("1.3.132.0.38", "sect571k1", "NIST K-571"),
    sect571r1("1.3.132.0.39", "sect571r1", "NIST B-571"),

    ECDH("1.3.132.1.12"),

    // OIW secsig 1.3.14.3.*
    OIW_DES_CBC("1.3.14.3.2.7", "DES/CBC", "DES"),

    OIW_DSA("1.3.14.3.2.12", "DSA"),

    OIW_JDK_SHA1withDSA("1.3.14.3.2.13", "SHA1withDSA"),

    OIW_SHA1withRSA_Odd("1.3.14.3.2.15", "SHA1withRSA"),

    DESede("1.3.14.3.2.17", "DESede"),

    SHA_1("1.3.14.3.2.26", "SHA-1", "SHA", "SHA1"),

    OIW_SHA1withDSA("1.3.14.3.2.27", "SHA1withDSA"),

    OIW_SHA1withRSA("1.3.14.3.2.29", "SHA1withRSA"),

    // Thawte 1.3.101.*
    X25519("1.3.101.110"),
    X448("1.3.101.111"),
    Ed25519("1.3.101.112"),
    Ed448("1.3.101.113"),

    // University College London (UCL) 0.9.2342.19200300.*
    UCL_UserID("0.9.2342.19200300.100.1.1"),
    UCL_DomainComponent("0.9.2342.19200300.100.1.25"),

    // Netscape 2.16.840.1.113730.*
    NETSCAPE_CertType("2.16.840.1.113730.1.1"),
    NETSCAPE_CertSequence("2.16.840.1.113730.2.5"),
    NETSCAPE_ExportApproved("2.16.840.1.113730.4.1"),

    // Oracle 2.16.840.1.113894.*
    ORACLE_TrustedKeyUsage("2.16.840.1.113894.746875.1.1"),  // Miscellaneous oids below which are legacy, and not well known
    // Consider removing them in future releases when their usage
    // have died out

    ITUX509_RSA("2.5.8.1.1", "RSA"),

    SkipIPAddress("1.3.6.1.4.1.42.2.11.2.1"),
    JAVASOFT_JDKKeyProtector("1.3.6.1.4.1.42.2.17.1.1"),
    JAVASOFT_JCEKeyProtector("1.3.6.1.4.1.42.2.19.1"),
    MICROSOFT_ExportApproved("1.3.6.1.4.1.311.10.3.3"),

    Blowfish("1.3.6.1.4.1.3029.1.1.2"),

    asn1_module("1.2.410.200046.1.1.1", "ASN1-module"),
    aria256_ecb("1.2.410.200046.1.1.11", "ARIA256/ECB"),
    aria256_cbc("1.2.410.200046.1.1.12", "ARIA256/CBC"),
    aria256_cfb("1.2.410.200046.1.1.13", "ARIA256/CFB"),
    aria256_ofb("1.2.410.200046.1.1.14", "ARIA256/OFB"),
    aria128_cbc("1.2.410.200046.1.1.2", "ARIA128/CBC"),
    aria128_cfb("1.2.410.200046.1.1.3", "ARIA128/CFB"),
    aria128_ofb("1.2.410.200046.1.1.4", "ARIA128/OFB"),
    aria192_ecb("1.2.410.200046.1.1.6", "ARIA192/ECB"),
    aria192_cbc("1.2.410.200046.1.1.7", "ARIA192/CBC"),
    aria192_cfb("1.2.410.200046.1.1.8", "ARIA192/CFB"),
    aria192_ofb("1.2.410.200046.1.1.9", "ARIA192/OFB"),
    BROKEN_GOST_R28147_89("1.2.643.2.2.13.0"),
    BROKEN_CryptoPro("1.2.643.2.2.13.1"),
    GOST_28147_89("1.2.643.2.2.21", "GOST-28147-89"),
    dstu7624_ecb_128("1.2.804.2.1.1.1.1.1.3.1.1", "DSTU7624/ECB-128"),
    dstu7624_ecb_256("1.2.804.2.1.1.1.1.1.3.1.2", "DSTU7624/ECB-256"),
    dstu7624_ecb_512("1.2.804.2.1.1.1.1.1.3.1.3", "DSTU7624/ECB-512"),
    dstu7624_ctr_128("1.2.804.2.1.1.1.1.1.3.2.1", "DSTU7624/CTR-128"),
    dstu7624_ctr_256("1.2.804.2.1.1.1.1.1.3.2.2", "DSTU7624/CTR-256"),
    dstu7624_ctr_512("1.2.804.2.1.1.1.1.1.3.2.3", "DSTU7624/CTR-512"),
    dstu7624_cfb_128("1.2.804.2.1.1.1.1.1.3.3.1", "DSTU7624/CFB-128"),
    dstu7624_cfb_256("1.2.804.2.1.1.1.1.1.3.3.2", "DSTU7624/CFB-256"),
    dstu7624_cfb_512("1.2.804.2.1.1.1.1.1.3.3.3", "DSTU7624/CFB-512"),
    dstu7624_cbc_128("1.2.804.2.1.1.1.1.1.3.5.1", "DSTU7624/CBC-128"),
    dstu7624_cbc_256("1.2.804.2.1.1.1.1.1.3.5.2", "DSTU7624/CBC-256"),
    dstu7624_cbc_512("1.2.804.2.1.1.1.1.1.3.5.3", "DSTU7624/CBC-512"),
    dstu7624_ofb_128("1.2.804.2.1.1.1.1.1.3.6.1", "DSTU7624/OFB-128"),
    dstu7624_ofb_256("1.2.804.2.1.1.1.1.1.3.6.2", "DSTU7624/OFB-256"),
    dstu7624_ofb_512("1.2.804.2.1.1.1.1.1.3.6.3", "DSTU7624/OFB-512"),
    dstu7624_ccm_128("1.2.804.2.1.1.1.1.1.3.8.1", "DSTU7624/CCM-128"),
    dstu7624_ccm_256("1.2.804.2.1.1.1.1.1.3.8.2", "DSTU7624/CCM-256"),
    dstu7624_ccm_512("1.2.804.2.1.1.1.1.1.3.8.3", "DSTU7624/CCM-512"),
    CMS3DESwrap("1.2.840.113549.1.9.16.3.6", "CMS3DESwrap"),
    des_CBC("1.3.14.3.2.7", "DES/CBC"),
    Joint_RSA("2.5.8.1.1", "Joint-RSA"),
    camellia128_wrap("1.2.392.200011.61.1.1.3.2", "camellia128/wrap"),
    camellia192_wrap("1.2.392.200011.61.1.1.3.3", "camellia192/wrap"),
    camellia256_wrap("1.2.392.200011.61.1.1.3.4", "camellia256/wrap"),
    id_aes192_CCM("2.16.840.1.101.3.4.1.27", "AES192/CCM"),
    aes256_CCM("2.16.840.1.101.3.4.1.47", "AES256/CCM"),
    aes128_CCM("2.16.840.1.101.3.4.1.7", "AES128/CCM");

    val algorithm: String
    val oid: String
    val aliases: Array<String>

    constructor(oid: String) {
        this.oid = oid
        this.algorithm = name // defaults to enum name
        this.aliases = arrayOf(oid, name)
    }

    constructor(
        oid: String,
        algorithm: String,
        vararg aliases: String
    ) {
        this.oid = oid
        this.algorithm = algorithm
        this.aliases = aliases.toList().toTypedArray()
    }

    companion object {
        fun findMatch(
            oidOrName: String
        ): SecureAlgorithmsMapping? = SecureAlgorithmsMapping.entries.find {
            it.oid == oidOrName || it.algorithm == oidOrName || it.aliases.contains(oidOrName)
        }
    }
}
