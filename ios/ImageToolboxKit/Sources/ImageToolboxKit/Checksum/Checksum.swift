import CryptoKit
import Foundation

/// Checksums and hashes offered by the checksum tool. Raw values are the display names.
///
/// The Android app takes its list from the BouncyCastle provider (64 names). This list is not the
/// same set: it covers the common cryptographic digests, CRC variants and the classic fast hashes.
public enum ChecksumAlgorithm: String, CaseIterable, Sendable {
  case md2 = "MD2"
  case sm3 = "SM3"
  case whirlpool = "Whirlpool"
  case tiger = "Tiger"
  case streebog256 = "GOST3411-2012-256"
  case streebog512 = "GOST3411-2012-512"
  case gost341194 = "GOST3411"
  case skein512 = "Skein-512-512"
  case md4 = "MD4"
  case md5 = "MD5"
  case sha1 = "SHA-1"
  case sha224 = "SHA-224"
  case sha256 = "SHA-256"
  case sha384 = "SHA-384"
  case sha512 = "SHA-512"
  case sha512t224 = "SHA-512/224"
  case sha512t256 = "SHA-512/256"
  case sha3224 = "SHA3-224"
  case sha3256 = "SHA3-256"
  case sha3384 = "SHA3-384"
  case sha3512 = "SHA3-512"
  case keccak224 = "Keccak-224"
  case keccak256 = "Keccak-256"
  case keccak384 = "Keccak-384"
  case keccak512 = "Keccak-512"
  case shake128 = "SHAKE128"
  case shake256 = "SHAKE256"
  case blake2b512 = "BLAKE2b-512"
  case blake2b384 = "BLAKE2b-384"
  case blake2b256 = "BLAKE2b-256"
  case blake2b160 = "BLAKE2b-160"
  case blake2s256 = "BLAKE2s-256"
  case blake2s224 = "BLAKE2s-224"
  case blake2s160 = "BLAKE2s-160"
  case blake2s128 = "BLAKE2s-128"
  case ripemd160 = "RIPEMD-160"
  case crc8Smbus = "CRC-8/SMBUS"
  case crc8Maxim = "CRC-8/MAXIM-DOW"
  case crc8DvbS2 = "CRC-8/DVB-S2"
  case crc16Arc = "CRC-16/ARC"
  case crc16CcittFalse = "CRC-16/CCITT-FALSE"
  case crc16Xmodem = "CRC-16/XMODEM"
  case crc16Modbus = "CRC-16/MODBUS"
  case crc16Kermit = "CRC-16/KERMIT"
  case crc16X25 = "CRC-16/X-25"
  case crc16Usb = "CRC-16/USB"
  case crc24OpenPGP = "CRC-24/OPENPGP"
  case crc32 = "CRC-32"
  case crc32C = "CRC-32C"
  case crc32Bzip2 = "CRC-32/BZIP2"
  case crc32Mpeg2 = "CRC-32/MPEG-2"
  case crc32Posix = "CRC-32/POSIX"
  case crc64Xz = "CRC-64/XZ"
  case crc64Ecma182 = "CRC-64/ECMA-182"
  case crc64GoIso = "CRC-64/GO-ISO"
  case adler32 = "Adler-32"
  case fletcher16 = "Fletcher-16"
  case fletcher32 = "Fletcher-32"
  case fnv132 = "FNV-1-32"
  case fnv1a32 = "FNV-1a-32"
  case fnv164 = "FNV-1-64"
  case fnv1a64 = "FNV-1a-64"
  case murmur332 = "MurmurHash3-32"
  case xxHash32 = "xxHash32"
  case xxHash64 = "xxHash64"
  case djb2 = "DJB2"
  case sdbm = "SDBM"
  case jenkinsOAAT = "Jenkins-OAAT"

  /// Raw digest bytes, big-endian for integer checksums.
  public func digest(of data: Data) -> [UInt8] {
    switch self {
    case .md2: MD2.hash(data)
    case .sm3: SM3.hash(data)
    case .whirlpool: Whirlpool.hash(data)
    case .tiger: Tiger.hash(data)
    case .streebog256: Streebog.hash256(data)
    case .streebog512: Streebog.hash512(data)
    case .gost341194: GOST341194.hash(data)
    case .skein512: Skein512.hash(data)
    case .md4: MD4.hash(data)
    case .md5: Array(Insecure.MD5.hash(data: data))
    case .sha1: Array(Insecure.SHA1.hash(data: data))
    case .sha224: SHA2Core.sha224(data)
    case .sha256: Array(SHA256.hash(data: data))
    case .sha384: Array(SHA384.hash(data: data))
    case .sha512: Array(SHA512.hash(data: data))
    case .sha512t224: SHA2Core.sha512t224(data)
    case .sha512t256: SHA2Core.sha512t256(data)
    case .sha3224: Keccak.hash(data, rate: 144, outputBytes: 28, domain: 0x06)
    case .sha3256: Keccak.hash(data, rate: 136, outputBytes: 32, domain: 0x06)
    case .sha3384: Keccak.hash(data, rate: 104, outputBytes: 48, domain: 0x06)
    case .sha3512: Keccak.hash(data, rate: 72, outputBytes: 64, domain: 0x06)
    case .keccak224: Keccak.hash(data, rate: 144, outputBytes: 28, domain: 0x01)
    case .keccak256: Keccak.hash(data, rate: 136, outputBytes: 32, domain: 0x01)
    case .keccak384: Keccak.hash(data, rate: 104, outputBytes: 48, domain: 0x01)
    case .keccak512: Keccak.hash(data, rate: 72, outputBytes: 64, domain: 0x01)
    case .shake128: Keccak.hash(data, rate: 168, outputBytes: 32, domain: 0x1F)
    case .shake256: Keccak.hash(data, rate: 136, outputBytes: 64, domain: 0x1F)
    case .blake2b512: BLAKE2.blake2b(data, outputBytes: 64)
    case .blake2b384: BLAKE2.blake2b(data, outputBytes: 48)
    case .blake2b256: BLAKE2.blake2b(data, outputBytes: 32)
    case .blake2b160: BLAKE2.blake2b(data, outputBytes: 20)
    case .blake2s256: BLAKE2.blake2s(data, outputBytes: 32)
    case .blake2s224: BLAKE2.blake2s(data, outputBytes: 28)
    case .blake2s160: BLAKE2.blake2s(data, outputBytes: 20)
    case .blake2s128: BLAKE2.blake2s(data, outputBytes: 16)
    case .ripemd160: RIPEMD160.hash(data)
    case .crc8Smbus: bytes(CRCCatalogue.crc8Smbus.checksum(data), width: 8)
    case .crc8Maxim: bytes(CRCCatalogue.crc8Maxim.checksum(data), width: 8)
    case .crc8DvbS2: bytes(CRCCatalogue.crc8DvbS2.checksum(data), width: 8)
    case .crc16Arc: bytes(CRCCatalogue.crc16Arc.checksum(data), width: 16)
    case .crc16CcittFalse: bytes(CRCCatalogue.crc16CcittFalse.checksum(data), width: 16)
    case .crc16Xmodem: bytes(CRCCatalogue.crc16Xmodem.checksum(data), width: 16)
    case .crc16Modbus: bytes(CRCCatalogue.crc16Modbus.checksum(data), width: 16)
    case .crc16Kermit: bytes(CRCCatalogue.crc16Kermit.checksum(data), width: 16)
    case .crc16X25: bytes(CRCCatalogue.crc16X25.checksum(data), width: 16)
    case .crc16Usb: bytes(CRCCatalogue.crc16Usb.checksum(data), width: 16)
    case .crc24OpenPGP: bytes(CRCCatalogue.crc24OpenPGP.checksum(data), width: 24)
    case .crc32: bytes(UInt64(CRC32.checksum(data)), width: 32)
    case .crc32C: bytes(CRCCatalogue.crc32C.checksum(data), width: 32)
    case .crc32Bzip2: bytes(CRCCatalogue.crc32Bzip2.checksum(data), width: 32)
    case .crc32Mpeg2: bytes(CRCCatalogue.crc32Mpeg2.checksum(data), width: 32)
    case .crc32Posix: bytes(CRCCatalogue.crc32Posix.checksum(data), width: 32)
    case .crc64Xz: bytes(CRCCatalogue.crc64Xz.checksum(data), width: 64)
    case .crc64Ecma182: bytes(CRCCatalogue.crc64Ecma182.checksum(data), width: 64)
    case .crc64GoIso: bytes(CRCCatalogue.crc64GoIso.checksum(data), width: 64)
    case .adler32: bytes(UInt64(Adler32.checksum(data)), width: 32)
    case .fletcher16: bytes(UInt64(SimpleHashes.fletcher16(data)), width: 16)
    case .fletcher32: bytes(UInt64(SimpleHashes.fletcher32(data)), width: 32)
    case .fnv132: bytes(UInt64(SimpleHashes.fnv132(data)), width: 32)
    case .fnv1a32: bytes(UInt64(SimpleHashes.fnv1a32(data)), width: 32)
    case .fnv164: bytes(SimpleHashes.fnv164(data), width: 64)
    case .fnv1a64: bytes(SimpleHashes.fnv1a64(data), width: 64)
    case .murmur332: bytes(UInt64(SimpleHashes.murmur332(data)), width: 32)
    case .xxHash32: bytes(UInt64(SimpleHashes.xxHash32(data)), width: 32)
    case .xxHash64: bytes(SimpleHashes.xxHash64(data), width: 64)
    case .djb2: bytes(UInt64(SimpleHashes.djb2(data)), width: 32)
    case .sdbm: bytes(UInt64(SimpleHashes.sdbm(data)), width: 32)
    case .jenkinsOAAT: bytes(UInt64(SimpleHashes.jenkinsOneAtATime(data)), width: 32)
    }
  }

  /// Lowercase hexadecimal digest.
  public func hexDigest(of data: Data) -> String {
    digest(of: data).map { String(format: "%02x", $0) }.joined()
  }

  private func bytes(_ value: UInt64, width: Int) -> [UInt8] {
    (0..<(width / 8)).reversed().map { UInt8((value >> UInt64($0 * 8)) & 0xFF) }
  }
}

/// CRC-32 as used by PNG, ZIP and gzip (reflected polynomial 0xEDB88320).
public enum CRC32 {
  private static let table: [UInt32] = (0..<256).map { index in
    var value = UInt32(index)
    for _ in 0..<8 {
      value = value & 1 == 1 ? 0xEDB8_8320 ^ (value >> 1) : value >> 1
    }
    return value
  }

  public static func checksum(_ data: Data) -> UInt32 {
    var crc: UInt32 = 0xFFFF_FFFF
    for byte in data {
      crc = table[Int((crc ^ UInt32(byte)) & 0xFF)] ^ (crc >> 8)
    }
    return crc ^ 0xFFFF_FFFF
  }
}

public enum Adler32 {
  public static func checksum(_ data: Data) -> UInt32 {
    var a: UInt32 = 1
    var b: UInt32 = 0
    for byte in data {
      a = (a + UInt32(byte)) % 65521
      b = (b + a) % 65521
    }
    return (b << 16) | a
  }
}
