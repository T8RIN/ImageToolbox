import Foundation

/// One CRC variant, described by the Rocksoft model: width, polynomial, initial value, final XOR,
/// and whether input and output bits are reflected.
struct CRCParameters: Sendable {
  let width: Int
  let polynomial: UInt64
  let initial: UInt64
  let xorOut: UInt64
  let reflectInput: Bool
  let reflectOutput: Bool

  func checksum(_ data: Data) -> UInt64 {
    let mask: UInt64 = width == 64 ? ~0 : (1 << UInt64(width)) - 1
    var crc: UInt64
    if reflectInput {
      let reversed = Self.reverseBits(polynomial, width: width)
      crc = Self.reverseBits(initial, width: width)
      for byte in data {
        crc ^= UInt64(byte)
        for _ in 0..<8 {
          crc = crc & 1 == 1 ? (crc >> 1) ^ reversed : crc >> 1
        }
      }
    } else {
      crc = initial
      let top: UInt64 = 1 << UInt64(width - 1)
      for byte in data {
        crc ^= UInt64(byte) << UInt64(width - 8)
        for _ in 0..<8 {
          crc = crc & top != 0 ? ((crc << 1) ^ polynomial) & mask : (crc << 1) & mask
        }
      }
    }
    if reflectInput != reflectOutput {
      crc = Self.reverseBits(crc, width: width)
    }
    return (crc ^ xorOut) & mask
  }

  static func reverseBits(_ value: UInt64, width: Int) -> UInt64 {
    var result: UInt64 = 0
    var input = value
    for _ in 0..<width {
      result = (result << 1) | (input & 1)
      input >>= 1
    }
    return result
  }
}

/// The CRC variants offered by the app. Check values are the catalogue values for "123456789".
enum CRCCatalogue {
  static let crc8Smbus = CRCParameters(
    width: 8, polynomial: 0x07, initial: 0, xorOut: 0, reflectInput: false, reflectOutput: false)
  static let crc8Maxim = CRCParameters(
    width: 8, polynomial: 0x31, initial: 0, xorOut: 0, reflectInput: true, reflectOutput: true)
  static let crc8DvbS2 = CRCParameters(
    width: 8, polynomial: 0xD5, initial: 0, xorOut: 0, reflectInput: false, reflectOutput: false)
  static let crc16Arc = CRCParameters(
    width: 16, polynomial: 0x8005, initial: 0, xorOut: 0, reflectInput: true, reflectOutput: true)
  static let crc16CcittFalse = CRCParameters(
    width: 16, polynomial: 0x1021, initial: 0xFFFF, xorOut: 0, reflectInput: false,
    reflectOutput: false)
  static let crc16Xmodem = CRCParameters(
    width: 16, polynomial: 0x1021, initial: 0, xorOut: 0, reflectInput: false, reflectOutput: false)
  static let crc16Modbus = CRCParameters(
    width: 16, polynomial: 0x8005, initial: 0xFFFF, xorOut: 0, reflectInput: true,
    reflectOutput: true)
  static let crc16Kermit = CRCParameters(
    width: 16, polynomial: 0x1021, initial: 0, xorOut: 0, reflectInput: true, reflectOutput: true)
  static let crc16X25 = CRCParameters(
    width: 16, polynomial: 0x1021, initial: 0xFFFF, xorOut: 0xFFFF, reflectInput: true,
    reflectOutput: true)
  static let crc16Usb = CRCParameters(
    width: 16, polynomial: 0x8005, initial: 0xFFFF, xorOut: 0xFFFF, reflectInput: true,
    reflectOutput: true)
  static let crc24OpenPGP = CRCParameters(
    width: 24, polynomial: 0x864CFB, initial: 0xB704CE, xorOut: 0, reflectInput: false,
    reflectOutput: false)
  static let crc32Bzip2 = CRCParameters(
    width: 32, polynomial: 0x04C1_1DB7, initial: 0xFFFF_FFFF, xorOut: 0xFFFF_FFFF,
    reflectInput: false, reflectOutput: false)
  static let crc32C = CRCParameters(
    width: 32, polynomial: 0x1EDC_6F41, initial: 0xFFFF_FFFF, xorOut: 0xFFFF_FFFF,
    reflectInput: true, reflectOutput: true)
  static let crc32Mpeg2 = CRCParameters(
    width: 32, polynomial: 0x04C1_1DB7, initial: 0xFFFF_FFFF, xorOut: 0, reflectInput: false,
    reflectOutput: false)
  static let crc32Posix = CRCParameters(
    width: 32, polynomial: 0x04C1_1DB7, initial: 0, xorOut: 0xFFFF_FFFF, reflectInput: false,
    reflectOutput: false)
  static let crc64Xz = CRCParameters(
    width: 64, polynomial: 0x42F0_E1EB_A9EA_3693, initial: ~0, xorOut: ~0, reflectInput: true,
    reflectOutput: true)
  static let crc64Ecma182 = CRCParameters(
    width: 64, polynomial: 0x42F0_E1EB_A9EA_3693, initial: 0, xorOut: 0, reflectInput: false,
    reflectOutput: false)
  static let crc64GoIso = CRCParameters(
    width: 64, polynomial: 0x1B, initial: ~0, xorOut: ~0, reflectInput: true, reflectOutput: true)
}
