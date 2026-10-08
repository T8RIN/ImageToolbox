import Foundation

/// SHA-2 family with custom initial values, so SHA-224 and the truncated SHA-512 variants can
/// be computed (CryptoKit offers SHA-256, SHA-384 and SHA-512 only). Constants are derived from
/// the prime-root definitions, not typed in.
enum SHA2Core {

  static let k256: [UInt32] = [
    0x428A_2F98, 0x7137_4491, 0xB5C0_FBCF, 0xE9B5_DBA5, 0x3956_C25B, 0x59F1_11F1, 0x923F_82A4,
    0xAB1C_5ED5, 0xD807_AA98, 0x1283_5B01, 0x2431_85BE, 0x550C_7DC3, 0x72BE_5D74, 0x80DE_B1FE,
    0x9BDC_06A7, 0xC19B_F174, 0xE49B_69C1, 0xEFBE_4786, 0x0FC1_9DC6, 0x240C_A1CC, 0x2DE9_2C6F,
    0x4A74_84AA, 0x5CB0_A9DC, 0x76F9_88DA, 0x983E_5152, 0xA831_C66D, 0xB003_27C8, 0xBF59_7FC7,
    0xC6E0_0BF3, 0xD5A7_9147, 0x06CA_6351, 0x1429_2967, 0x27B7_0A85, 0x2E1B_2138, 0x4D2C_6DFC,
    0x5338_0D13, 0x650A_7354, 0x766A_0ABB, 0x81C2_C92E, 0x9272_2C85, 0xA2BF_E8A1, 0xA81A_664B,
    0xC24B_8B70, 0xC76C_51A3, 0xD192_E819, 0xD699_0624, 0xF40E_3585, 0x106A_A070, 0x19A4_C116,
    0x1E37_6C08, 0x2748_774C, 0x34B0_BCB5, 0x391C_0CB3, 0x4ED8_AA4A, 0x5B9C_CA4F, 0x682E_6FF3,
    0x748F_82EE, 0x78A5_636F, 0x84C8_7814, 0x8CC7_0208, 0x90BE_FFFA, 0xA450_6CEB, 0xBEF9_A3F7,
    0xC671_78F2,
  ]

  static let k512: [UInt64] = [
    0x428A_2F98_D728_AE22, 0x7137_4491_23EF_65CD, 0xB5C0_FBCF_EC4D_3B2F, 0xE9B5_DBA5_8189_DBBC,
    0x3956_C25B_F348_B538, 0x59F1_11F1_B605_D019, 0x923F_82A4_AF19_4F9B, 0xAB1C_5ED5_DA6D_8118,
    0xD807_AA98_A303_0242, 0x1283_5B01_4570_6FBE, 0x2431_85BE_4EE4_B28C, 0x550C_7DC3_D5FF_B4E2,
    0x72BE_5D74_F27B_896F, 0x80DE_B1FE_3B16_96B1, 0x9BDC_06A7_25C7_1235, 0xC19B_F174_CF69_2694,
    0xE49B_69C1_9EF1_4AD2, 0xEFBE_4786_384F_25E3, 0x0FC1_9DC6_8B8C_D5B5, 0x240C_A1CC_77AC_9C65,
    0x2DE9_2C6F_592B_0275, 0x4A74_84AA_6EA6_E483, 0x5CB0_A9DC_BD41_FBD4, 0x76F9_88DA_8311_53B5,
    0x983E_5152_EE66_DFAB, 0xA831_C66D_2DB4_3210, 0xB003_27C8_98FB_213F, 0xBF59_7FC7_BEEF_0EE4,
    0xC6E0_0BF3_3DA8_8FC2, 0xD5A7_9147_930A_A725, 0x06CA_6351_E003_826F, 0x1429_2967_0A0E_6E70,
    0x27B7_0A85_46D2_2FFC, 0x2E1B_2138_5C26_C926, 0x4D2C_6DFC_5AC4_2AED, 0x5338_0D13_9D95_B3DF,
    0x650A_7354_8BAF_63DE, 0x766A_0ABB_3C77_B2A8, 0x81C2_C92E_47ED_AEE6, 0x9272_2C85_1482_353B,
    0xA2BF_E8A1_4CF1_0364, 0xA81A_664B_BC42_3001, 0xC24B_8B70_D0F8_9791, 0xC76C_51A3_0654_BE30,
    0xD192_E819_D6EF_5218, 0xD699_0624_5565_A910, 0xF40E_3585_5771_202A, 0x106A_A070_32BB_D1B8,
    0x19A4_C116_B8D2_D0C8, 0x1E37_6C08_5141_AB53, 0x2748_774C_DF8E_EB99, 0x34B0_BCB5_E19B_48A8,
    0x391C_0CB3_C5C9_5A63, 0x4ED8_AA4A_E341_8ACB, 0x5B9C_CA4F_7763_E373, 0x682E_6FF3_D6B2_B8A3,
    0x748F_82EE_5DEF_B2FC, 0x78A5_636F_4317_2F60, 0x84C8_7814_A1F0_AB72, 0x8CC7_0208_1A64_39EC,
    0x90BE_FFFA_2363_1E28, 0xA450_6CEB_DE82_BDE9, 0xBEF9_A3F7_B2C6_7915, 0xC671_78F2_E372_532B,
    0xCA27_3ECE_EA26_619C, 0xD186_B8C7_21C0_C207, 0xEADA_7DD6_CDE0_EB1E, 0xF57D_4F7F_EE6E_D178,
    0x06F0_67AA_7217_6FBA, 0x0A63_7DC5_A2C8_98A6, 0x113F_9804_BEF9_0DAE, 0x1B71_0B35_131C_471B,
    0x28DB_77F5_2304_7D84, 0x32CA_AB7B_40C7_2493, 0x3C9E_BE0A_15C9_BEBC, 0x431D_67C4_9C10_0D4C,
    0x4CC5_D4BE_CB3E_42B6, 0x597F_299C_FC65_7E2A, 0x5FCB_6FAB_3AD6_FAEC, 0x6C44_198C_4A47_5817,
  ]

  static let iv224: [UInt32] = [
    0xC105_9ED8, 0x367C_D507, 0x3070_DD17, 0xF70E_5939, 0xFFC0_0B31, 0x6858_1511, 0x64F9_8FA7,
    0xBEFA_4FA4,
  ]

  static let iv512t224: [UInt64] = [
    0x8C3D_37C8_1954_4DA2, 0x73E1_9966_89DC_D4D6, 0x1DFA_B7AE_32FF_9C82, 0x679D_D514_582F_9FCF,
    0x0F6D_2B69_7BD4_4DA8, 0x77E3_6F73_04C4_8942, 0x3F9D_85A8_6A1D_36C8, 0x1112_E6AD_91D6_92A1,
  ]

  static let iv512t256: [UInt64] = [
    0x2231_2194_FC2B_F72C, 0x9F55_5FA3_C84C_64C2, 0x2393_B86B_6F53_B151, 0x9638_7719_5940_EABD,
    0x9628_3EE2_A88E_FFE3, 0xBE5E_1E25_5386_3992, 0x2B01_99FC_2C85_B8AA, 0x0EB7_2DDC_81C5_2CA2,
  ]

  /// SHA-224 is SHA-256 with another initial value, truncated to 28 bytes.
  static func sha224(_ data: Data) -> [UInt8] {
    Array(sha256(data, initial: iv224).prefix(28))
  }

  static func sha512t224(_ data: Data) -> [UInt8] {
    Array(sha512(data, initial: iv512t224).prefix(28))
  }

  static func sha512t256(_ data: Data) -> [UInt8] {
    Array(sha512(data, initial: iv512t256).prefix(32))
  }

  static func sha256(_ data: Data, initial: [UInt32]) -> [UInt8] {
    var state = initial
    var message = [UInt8](data)
    let bitLength = UInt64(message.count) &* 8
    message.append(0x80)
    while message.count % 64 != 56 { message.append(0) }
    for shift in stride(from: 56, through: 0, by: -8) {
      message.append(UInt8((bitLength >> UInt64(shift)) & 0xFF))
    }

    for block in stride(from: 0, to: message.count, by: 64) {
      var w = [UInt32](repeating: 0, count: 64)
      for index in 0..<16 {
        let base = block + index * 4
        w[index] =
          UInt32(message[base]) << 24 | UInt32(message[base + 1]) << 16
          | UInt32(message[base + 2]) << 8 | UInt32(message[base + 3])
      }
      for index in 16..<64 {
        let s0 = rotr32(w[index - 15], 7) ^ rotr32(w[index - 15], 18) ^ (w[index - 15] >> 3)
        let s1 = rotr32(w[index - 2], 17) ^ rotr32(w[index - 2], 19) ^ (w[index - 2] >> 10)
        w[index] = w[index - 16] &+ s0 &+ w[index - 7] &+ s1
      }

      var a = state[0]
      var b = state[1]
      var c = state[2]
      var d = state[3]
      var e = state[4]
      var f = state[5]
      var g = state[6]
      var h = state[7]
      for index in 0..<64 {
        let s1 = rotr32(e, 6) ^ rotr32(e, 11) ^ rotr32(e, 25)
        let choose = (e & f) ^ (~e & g)
        let temp1 = h &+ s1 &+ choose &+ k256[index] &+ w[index]
        let s0 = rotr32(a, 2) ^ rotr32(a, 13) ^ rotr32(a, 22)
        let majority = (a & b) ^ (a & c) ^ (b & c)
        let temp2 = s0 &+ majority
        h = g
        g = f
        f = e
        e = d &+ temp1
        d = c
        c = b
        b = a
        a = temp1 &+ temp2
      }
      state[0] = state[0] &+ a
      state[1] = state[1] &+ b
      state[2] = state[2] &+ c
      state[3] = state[3] &+ d
      state[4] = state[4] &+ e
      state[5] = state[5] &+ f
      state[6] = state[6] &+ g
      state[7] = state[7] &+ h
    }

    var output: [UInt8] = []
    for word in state {
      for shift in stride(from: 24, through: 0, by: -8) {
        output.append(UInt8((word >> UInt32(shift)) & 0xFF))
      }
    }
    return output
  }

  static func sha512(_ data: Data, initial: [UInt64]) -> [UInt8] {
    var state = initial
    var message = [UInt8](data)
    let bitLength = UInt64(message.count) &* 8
    message.append(0x80)
    while message.count % 128 != 112 { message.append(0) }
    for _ in 0..<8 { message.append(0) }
    for shift in stride(from: 56, through: 0, by: -8) {
      message.append(UInt8((bitLength >> UInt64(shift)) & 0xFF))
    }

    for block in stride(from: 0, to: message.count, by: 128) {
      var w = [UInt64](repeating: 0, count: 80)
      for index in 0..<16 {
        var word: UInt64 = 0
        for byte in 0..<8 {
          word = word << 8 | UInt64(message[block + index * 8 + byte])
        }
        w[index] = word
      }
      for index in 16..<80 {
        let s0 = rotr64(w[index - 15], 1) ^ rotr64(w[index - 15], 8) ^ (w[index - 15] >> 7)
        let s1 = rotr64(w[index - 2], 19) ^ rotr64(w[index - 2], 61) ^ (w[index - 2] >> 6)
        w[index] = w[index - 16] &+ s0 &+ w[index - 7] &+ s1
      }

      var a = state[0]
      var b = state[1]
      var c = state[2]
      var d = state[3]
      var e = state[4]
      var f = state[5]
      var g = state[6]
      var h = state[7]
      for index in 0..<80 {
        let s1 = rotr64(e, 14) ^ rotr64(e, 18) ^ rotr64(e, 41)
        let choose = (e & f) ^ (~e & g)
        let temp1 = h &+ s1 &+ choose &+ k512[index] &+ w[index]
        let s0 = rotr64(a, 28) ^ rotr64(a, 34) ^ rotr64(a, 39)
        let majority = (a & b) ^ (a & c) ^ (b & c)
        let temp2 = s0 &+ majority
        h = g
        g = f
        f = e
        e = d &+ temp1
        d = c
        c = b
        b = a
        a = temp1 &+ temp2
      }
      state[0] = state[0] &+ a
      state[1] = state[1] &+ b
      state[2] = state[2] &+ c
      state[3] = state[3] &+ d
      state[4] = state[4] &+ e
      state[5] = state[5] &+ f
      state[6] = state[6] &+ g
      state[7] = state[7] &+ h
    }

    var output: [UInt8] = []
    for word in state {
      for shift in stride(from: 56, through: 0, by: -8) {
        output.append(UInt8((word >> UInt64(shift)) & 0xFF))
      }
    }
    return output
  }

  private static func rotr32(_ value: UInt32, _ count: UInt32) -> UInt32 {
    (value >> count) | (value << (32 - count))
  }

  private static func rotr64(_ value: UInt64, _ count: UInt64) -> UInt64 {
    (value >> count) | (value << (64 - count))
  }
}
