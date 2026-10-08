import Foundation
import Testing

@testable import ImageToolboxKit

/// Reference digests computed with independent libraries (hashlib, pycryptodome, crc, xxhash,
/// mmh3, zlib) for five inputs: empty, "abc", "123456789", a sentence, and 1000 bytes of "a".
@Suite("Checksum algorithms against reference vectors")
struct ChecksumVectorTests {

  private static let inputs: [String: Data] = [
    "empty": Data(),
    "abc": Data("abc".utf8),
    "check": Data("123456789".utf8),
    "quick": Data("The quick brown fox jumps over the lazy dog".utf8),
    "long": Data(repeating: 0x61, count: 1000),
  ]

  private static let vectors: [(ChecksumAlgorithm, [String: String])] = [
    (
      .md4,
      [
        "empty": "31d6cfe0d16ae931b73c59d7e0c089c0", "abc": "a448017aaf21d8525fc10ae87aa6729d",
        "check": "2ae523785d0caf4d2fb557c12016185c", "quick": "1bee69a46ba811185c194762abaeae90",
        "long": "5f1bf26a8067c9159b91f1440f7c9e8a",
      ]
    ),
    (
      .md5,
      [
        "empty": "d41d8cd98f00b204e9800998ecf8427e", "abc": "900150983cd24fb0d6963f7d28e17f72",
        "check": "25f9e794323b453885f5181f1b624d0b", "quick": "9e107d9d372bb6826bd81d3542a419d6",
        "long": "cabe45dcc9ae5b66ba86600cca6b8ba8",
      ]
    ),
    (
      .sha1,
      [
        "empty": "da39a3ee5e6b4b0d3255bfef95601890afd80709",
        "abc": "a9993e364706816aba3e25717850c26c9cd0d89d",
        "check": "f7c3bc1d808e04732adf679965ccc34ca7ae3441",
        "quick": "2fd4e1c67a2d28fced849ee1bb76e7391b93eb12",
        "long": "291e9a6c66994949b57ba5e650361e98fc36b1ba",
      ]
    ),
    (
      .sha224,
      [
        "empty": "d14a028c2a3a2bc9476102bb288234c415a2b01f828ea62ac5b3e42f",
        "abc": "23097d223405d8228642a477bda255b32aadbce4bda0b3f7e36c9da7",
        "check": "9b3e61bf29f17c75572fae2e86e17809a4513d07c8a18152acf34521",
        "quick": "730e109bd7a8a32b1cb9d9a09aa2325d2430587ddbc0c38bad911525",
        "long": "4e8f0ce90b64661a2b5e84be6d93a7d9b76871062f1814433d04a03d",
      ]
    ),
    (
      .sha256,
      [
        "empty": "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
        "abc": "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
        "check": "15e2b0d3c33891ebb0f1ef609ec419420c20e320ce94c65fbc8c3312448eb225",
        "quick": "d7a8fbb307d7809469ca9abcb0082e4f8d5651e46d3cdb762d02d0bf37c9e592",
        "long": "41edece42d63e8d9bf515a9ba6932e1c20cbc9f5a5d134645adb5db1b9737ea3",
      ]
    ),
    (
      .sha384,
      [
        "empty":
          "38b060a751ac96384cd9327eb1b1e36a21fdb71114be07434c0cc7bf63f6e1da274edebfe76f65fbd51ad2f14898b95b",
        "abc":
          "cb00753f45a35e8bb5a03d699ac65007272c32ab0eded1631a8b605a43ff5bed8086072ba1e7cc2358baeca134c825a7",
        "check":
          "eb455d56d2c1a69de64e832011f3393d45f3fa31d6842f21af92d2fe469c499da5e3179847334a18479c8d1dedea1be3",
        "quick":
          "ca737f1014a48f4c0b6dd43cb177b0afd9e5169367544c494011e3317dbf9a509cb1e5dc1e85a941bbee3d7f2afbc9b1",
        "long":
          "f54480689c6b0b11d0303285d9a81b21a93bca6ba5a1b4472765dca4da45ee328082d469c650cd3b61b16d3266ab8ced",
      ]
    ),
    (
      .sha512,
      [
        "empty":
          "cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e",
        "abc":
          "ddaf35a193617abacc417349ae20413112e6fa4e89a97ea20a9eeee64b55d39a2192992a274fc1a836ba3c23a3feebbd454d4423643ce80e2a9ac94fa54ca49f",
        "check":
          "d9e6762dd1c8eaf6d61b3c6192fc408d4d6d5f1176d0c29169bc24e71c3f274ad27fcd5811b313d681f7e55ec02d73d499c95455b6b5bb503acf574fba8ffe85",
        "quick":
          "07e547d9586f6a73f73fbac0435ed76951218fb7d0c8d788a309d785436bbb642e93a252a954f23912547d1e8a3b5ed6e1bfd7097821233fa0538f3db854fee6",
        "long":
          "67ba5535a46e3f86dbfbed8cbbaf0125c76ed549ff8b0b9e03e0c88cf90fa634fa7b12b47d77b694de488ace8d9a65967dc96df599727d3292a8d9d447709c97",
      ]
    ),
    (
      .sha512t224,
      [
        "empty": "6ed0dd02806fa89e25de060c19d3ac86cabb87d6a0ddd05c333b84f4",
        "abc": "4634270f707b6a54daae7530460842e20e37ed265ceee9a43e8924aa",
        "check": "f2a68a474bcbea375e9fc62eaab7b81fefbda64bb1c72d72e7c27314",
        "quick": "944cd2847fb54558d4775db0485a50003111c8e5daa63fe722c6aa37",
        "long": "ffdfa284ae9e562222e2a37cd683823f7e669f3636477701f4ce9abe",
      ]
    ),
    (
      .sha512t256,
      [
        "empty": "c672b8d1ef56ed28ab87c3622c5114069bdd3ad7b8f9737498d0c01ecef0967a",
        "abc": "53048e2681941ef99b2e29b76b4c7dabe4c2d0c634fc6d46e0e2f13107e7af23",
        "check": "1877345237853a31ad79e14c1fcb0ddcd3df9973b61af7f906e4b4d052cc9416",
        "quick": "dd9d67b371519c339ed8dbd25af90e976a1eeefd4ad3d889005e532fc5bef04d",
        "long": "40eb4a70d4d69815407a9e272f0101cd67e3d11262a4a0bfc087712749c7fb53",
      ]
    ),
    (
      .sha3224,
      [
        "empty": "6b4e03423667dbb73b6e15454f0eb1abd4597f9a1b078e3f5b5a6bc7",
        "abc": "e642824c3f8cf24ad09234ee7d3c766fc9a3a5168d0c94ad73b46fdf",
        "check": "5795c3d628fd638c9835a4c79a55809f265068c88729a1a3fcdf8522",
        "quick": "d15dadceaa4d5d7bb3b48f446421d542e08ad8887305e28d58335795",
        "long": "2461344b84416db8fe01c2a4966fea019590c231dd5724c1bfc26745",
      ]
    ),
    (
      .sha3256,
      [
        "empty": "a7ffc6f8bf1ed76651c14756a061d662f580ff4de43b49fa82d80a4b80f8434a",
        "abc": "3a985da74fe225b2045c172d6bd390bd855f086e3e9d525b46bfe24511431532",
        "check": "87cd084d190e436f147322b90e7384f6a8e0676c99d21ef519ea718e51d45f9c",
        "quick": "69070dda01975c8c120c3aada1b282394e7f032fa9cf32f4cb2259a0897dfc04",
        "long": "8f3934e6f7a15698fe0f396b95d8c4440929a8fa6eae140171c068b4549fbf81",
      ]
    ),
    (
      .sha3384,
      [
        "empty":
          "0c63a75b845e4f7d01107d852e4c2485c51a50aaaa94fc61995e71bbee983a2ac3713831264adb47fb6bd1e058d5f004",
        "abc":
          "ec01498288516fc926459f58e2c6ad8df9b473cb0fc08c2596da7cf0e49be4b298d88cea927ac7f539f1edf228376d25",
        "check":
          "8b90ede4d095409f1a12492c2520599683a9478dc70b7566d23b3e41ece8538c6cde92382a5e38786490375c54672abf",
        "quick":
          "7063465e08a93bce31cd89d2e3ca8f602498696e253592ed26f07bf7e703cf328581e1471a7ba7ab119b1a9ebdf8be41",
        "long":
          "ccf4495ff20b4b33a1cc1917f9f0fe0fcb5e3d08e542cf4d4a90dd950b748e7e1cc07d2f3b36d62dd240724417cdd81b",
      ]
    ),
    (
      .sha3512,
      [
        "empty":
          "a69f73cca23a9ac5c8b567dc185a756e97c982164fe25859e0d1dcc1475c80a615b2123af1f5f94c11e3e9402c3ac558f500199d95b6d3e301758586281dcd26",
        "abc":
          "b751850b1a57168a5693cd924b6b096e08f621827444f70d884f5d0240d2712e10e116e9192af3c91a7ec57647e3934057340b4cf408d5a56592f8274eec53f0",
        "check":
          "e1e44d20556e97a180b6dd3ed7ae5c465cafd553fa8747dca038fb95635b77a37318f7ddf7aec1f6c3c14bb160ba2497007decf38dd361cab199e3b8c8fe1f5c",
        "quick":
          "01dedd5de4ef14642445ba5f5b97c15e47b9ad931326e4b0727cd94cefc44fff23f07bf543139939b49128caf436dc1bdee54fcb24023a08d9403f9b4bf0d450",
        "long":
          "ac7e95cc95aa7f24aaa95e040ca0c79b39cd9cc84a10abb84ddd8dd5e4b45cf96543aaa70d0ef99fbf8d2769639981ee1fd0b0276f4756b9d504d0b7de19b700",
      ]
    ),
    (
      .keccak224,
      [
        "empty": "f71837502ba8e10837bdd8d365adb85591895602fc552b48b7390abd",
        "abc": "c30411768506ebe1c2871b1ee2e87d38df342317300a9b97a95ec6a8",
        "check": "06471de6c635a88e7470284b2c2ebf9bd7e5e888cbbd128c21cb8308",
        "quick": "310aee6b30c47350576ac2873fa89fd190cdc488442f3ef654cf23fe",
        "long": "0cfe02a6b3c619d5c07af0f52281206c46bf11c71d08e02c18d98419",
      ]
    ),
    (
      .keccak256,
      [
        "empty": "c5d2460186f7233c927e7db2dcc703c0e500b653ca82273b7bfad8045d85a470",
        "abc": "4e03657aea45a94fc7d47ba826c8d667c0d1e6e33a64a036ec44f58fa12d6c45",
        "check": "2a359feeb8e488a1af2c03b908b3ed7990400555db73e1421181d97cac004d48",
        "quick": "4d741b6f1eb29cb2a9b9911c82f56fa8d73b04959d3d9d222895df6c0b28aa15",
        "long": "b6a4ac1f51884d71f30fa397a5e155de3099e11fc0edef5d08b646e621e19de9",
      ]
    ),
    (
      .keccak384,
      [
        "empty":
          "2c23146a63a29acf99e73b88f8c24eaa7dc60aa771780ccc006afbfa8fe2479b2dd2b21362337441ac12b515911957ff",
        "abc":
          "f7df1165f033337be098e7d288ad6a2f74409d7a60b49c36642218de161b1f99f8c681e4afaf31a34db29fb763e3c28e",
        "check":
          "efccae72ce14656c434751cf737e70a57ab8dd2c76f5abe01e52770affd77b66d2b80977724a00a6d971b702906f8032",
        "quick":
          "283990fa9d5fb731d786c5bbee94ea4db4910f18c62c03d173fc0a5e494422e8a0b3da7574dae7fa0baf005e504063b3",
        "long":
          "b3e677e3392289abe062ccb971163840ea6f728754c72f8e3f80ad28358c4cb0f8a0d6f3995bf09349fccfda19db0ef4",
      ]
    ),
    (
      .keccak512,
      [
        "empty":
          "0eab42de4c3ceb9235fc91acffe746b29c29a8c366b7c60e4e67c466f36a4304c00fa9caf9d87976ba469bcbe06713b435f091ef2769fb160cdab33d3670680e",
        "abc":
          "18587dc2ea106b9a1563e32b3312421ca164c7f1f07bc922a9c83d77cea3a1e5d0c69910739025372dc14ac9642629379540c17e2a65b19d77aa511a9d00bb96",
        "check":
          "40b787e94778266fb196a73b7a77edf9de2ef172451a2b87531324812250df8f26fcc11e69b35afddbe639956c96153e71363f97010bc99405dd2d77b8c41986",
        "quick":
          "d135bb84d0439dbac432247ee573a23ea7d3c9deb2a968eb31d47c4fb45f1ef4422d6c531b5b9bd6f449ebcc449ea94d0a8f05f62130fda612da53c79659f609",
        "long":
          "963bcff88a13a6f65f8952d8c13fff587b51baa50996712a0ef6779ff148459f28788ee9aada5616972be9036c0e8dec7bb886cea368bbfde73fc5f86c32a561",
      ]
    ),
    (
      .shake128,
      [
        "empty": "7f9c2ba4e88f827d616045507605853ed73b8093f6efbc88eb1a6eacfa66ef26",
        "abc": "5881092dd818bf5cf8a3ddb793fbcba74097d5c526a6d35f97b83351940f2cc8",
        "check": "1aca6b9e651b5f20079a305ca8f86d39b9451c4c32873f95f8b315834bd5f272",
        "quick": "f4202e3c5852f9182a0430fd8144f0a74b95e7417ecae17db0f8cfeed0e3e66e",
        "long": "c340a5d49d81d4dcf3e6fa3387202b9b67e8ab78482f9956be63d1f09b9cb436",
      ]
    ),
    (
      .shake256,
      [
        "empty":
          "46b9dd2b0ba88d13233b3feb743eeb243fcd52ea62b81b82b50c27646ed5762fd75dc4ddd8c0f200cb05019d67b592f6fc821c49479ab48640292eacb3b7c4be",
        "abc":
          "483366601360a8771c6863080cc4114d8db44530f8f1e1ee4f94ea37e78b5739d5a15bef186a5386c75744c0527e1faa9f8726e462a12a4feb06bd8801e751e4",
        "check":
          "24347b9c4b6da2fc9cde08c87f33edd2e603c8dcd6840e6b3920f62b1dd69d7bc4655a9e6f0ee6255940380dcd1488dbca3e796ae58a2234cc31cd61dfd1eb56",
        "quick":
          "2f671343d9b2e1604dc9dcf0753e5fe15c7c64a0d283cbbf722d411a0e36f6ca1d01d1369a23539cd80f7c054b6e5daf9c962cad5b8ed5bd11998b40d5734442",
        "long":
          "e262331ad290c96ab1c0fa045470244b415ba6696a934d60f2999b8e92aaa24ee8eb039abd7af7d64fde39fa73267b02fdd3a50e1b8651b846a9bb2cc4f344c5",
      ]
    ),
    (
      .blake2b512,
      [
        "empty":
          "786a02f742015903c6c6fd852552d272912f4740e15847618a86e217f71f5419d25e1031afee585313896444934eb04b903a685b1448b755d56f701afe9be2ce",
        "abc":
          "ba80a53f981c4d0d6a2797b69f12f6e94c212f14685ac4b74b12bb6fdbffa2d17d87c5392aab792dc252d5de4533cc9518d38aa8dbf1925ab92386edd4009923",
        "check":
          "f5ab8bafa6f2f72b431188ac38ae2de7bb618fb3d38b6cbf639defcdd5e10a86b22fccff571da37e42b23b80b657ee4d936478f582280a87d6dbb1da73f5c47d",
        "quick":
          "a8add4bdddfd93e4877d2746e62817b116364a1fa7bc148d95090bc7333b3673f82401cf7aa2e4cb1ecd90296e3f14cb5413f8ed77be73045b13914cdcd6a918",
        "long":
          "d6a69459fe93fc6b9537ed4336e5099e0dcca3e97290a412500ed7a0daffb03d80cf3650a20e0591f748e10c3c534945ee83d5f2c9722f1a68d98b8c01af23fd",
      ]
    ),
    (
      .blake2b384,
      [
        "empty":
          "b32811423377f52d7862286ee1a72ee540524380fda1724a6f25d7978c6fd3244a6caf0498812673c5e05ef583825100",
        "abc":
          "6f56a82c8e7ef526dfe182eb5212f7db9df1317e57815dbda46083fc30f54ee6c66ba83be64b302d7cba6ce15bb556f4",
        "check":
          "80f35fcfa2f3eba9cac3287c2d95d02b5f179a65dfc60c9f48275a459919d2b52bdb5877dcd7e21e9ff95a551b87fc36",
        "quick":
          "b7c81b228b6bd912930e8f0b5387989691c1cee1e65aade4da3b86a3c9f678fc8018f6ed9e2906720c8d2a3aeda9c03d",
        "long":
          "60a160160a960409a363fd134b23e029b7ba77b1c3b2c4bb13682074a52af31cdbcf2ba8c953026ea31174a542eb4370",
      ]
    ),
    (
      .blake2b256,
      [
        "empty": "0e5751c026e543b2e8ab2eb06099daa1d1e5df47778f7787faab45cdf12fe3a8",
        "abc": "bddd813c634239723171ef3fee98579b94964e3bb1cb3e427262c8c068d52319",
        "check": "16e0bf1f85594a11e75030981c0b670370b3ad83a43f49ae58a2fd6f6513cde9",
        "quick": "01718cec35cd3d796dd00020e0bfecb473ad23457d063b75eff29c0ffa2e58a9",
        "long": "e00b0ddbf1e2cdaf5c898e1a5e8826ea3a2c339bcf2a478da2e5fca9ff126672",
      ]
    ),
    (
      .blake2b160,
      [
        "empty": "3345524abf6bbe1809449224b5972c41790b6cf2",
        "abc": "384264f676f39536840523f284921cdc68b6846b",
        "check": "f34f0bb8223b921e1fffeecca699db4a66edf1a8",
        "quick": "3c523ed102ab45a37d54f5610d5a983162fde84f",
        "long": "0c8c64f74ac7d623cfbfc452aefb31d8b8dadad9",
      ]
    ),
    (
      .blake2s256,
      [
        "empty": "69217a3079908094e11121d042354a7c1f55b6482ca1a51e1b250dfd1ed0eef9",
        "abc": "508c5e8c327c14e2e1a72ba34eeb452f37458b209ed63a294d999b4c86675982",
        "check": "7acc2dd21a2909140507f37396acce906864b5f118dfa766b107962b7a82a0d4",
        "quick": "606beeec743ccbeff6cbcdf5d5302aa855c256c29b88c8ed331ea1a6bf3c8812",
        "long": "a4691c2bf852334ece63c024234338fc6c150bdf04fa3f6e0e4c5209b326438d",
      ]
    ),
    (
      .blake2s224,
      [
        "empty": "1fa1291e65248b37b3433475b2a0dd63d54a11ecc4e3e034e7bc1ef4",
        "abc": "0b033fc226df7abde29f67a05d3dc62cf271ef3dfea4d387407fbd55",
        "check": "8b5b64be12d131e47ea3e1d7e2de47efb806461f6023c281f9e23cad",
        "quick": "e4e5cb6c7cae41982b397bf7b7d2d9d1949823ae78435326e8db4912",
        "long": "3e258a09784feddcdd23f20e93d3b6166316dc34f4b721873512a4a8",
      ]
    ),
    (
      .blake2s160,
      [
        "empty": "354c9c33f735962418bdacb9479873429c34916f",
        "abc": "5ae3b99be29b01834c3b508521ede60438f8de17",
        "check": "57c99b8345acf5a7b22f15db7742a36d4cb8313b",
        "quick": "5a604fec9713c369e84b0ed68daed7d7504ef240",
        "long": "a64df1aeae313d76329b2238ea8f35327ba94f32",
      ]
    ),
    (
      .blake2s128,
      [
        "empty": "64550d6ffe2c0a01a14aba1eade0200c", "abc": "aa4938119b1dc7b87cbad0ffd200d0ae",
        "check": "dce1c41568c6aa166e2f8eafce34e617", "quick": "96fd07258925748a0d2fb1c8a1167a73",
        "long": "24bbd9af662a7849f410eb27c8d99cdc",
      ]
    ),
    (
      .ripemd160,
      [
        "empty": "9c1185a5c5e9fc54612808977ee8f548b2258d31",
        "abc": "8eb208f7e05d987a9b044a8e98c6b087f15a0bfc",
        "check": "d3d0379126c1e5e0ba70ad6e5e53ff6aeab9f4fa",
        "quick": "37f332f68db77bd9d7edd4969571ad671cf9dd3b",
        "long": "aa69deee9a8922e92f8105e007f76110f381e9cf",
      ]
    ),
    (.crc8Smbus, ["empty": "00", "abc": "5f", "check": "f4", "quick": "c1", "long": "f5"]),
    (.crc8Maxim, ["empty": "00", "abc": "42", "check": "a1", "quick": "16", "long": "d8"]),
    (.crc8DvbS2, ["empty": "00", "abc": "5a", "check": "bc", "quick": "2a", "long": "9c"]),
    (
      .crc16Arc, ["empty": "0000", "abc": "9738", "check": "bb3d", "quick": "fcdf", "long": "758f"]
    ),
    (
      .crc16CcittFalse,
      ["empty": "ffff", "abc": "514a", "check": "29b1", "quick": "8fdd", "long": "9d4c"]
    ),
    (
      .crc16Xmodem,
      ["empty": "0000", "abc": "9dd6", "check": "31c3", "quick": "f0c8", "long": "98ef"]
    ),
    (
      .crc16Modbus,
      ["empty": "ffff", "abc": "5749", "check": "4b37", "quick": "a89c", "long": "7edb"]
    ),
    (
      .crc16Kermit,
      ["empty": "0000", "abc": "58e9", "check": "2189", "quick": "c459", "long": "95c0"]
    ),
    (
      .crc16X25, ["empty": "0000", "abc": "9e25", "check": "906e", "quick": "9358", "long": "af9f"]
    ),
    (
      .crc16Usb, ["empty": "0000", "abc": "a8b6", "check": "b4c8", "quick": "5763", "long": "8124"]
    ),
    (
      .crc24OpenPGP,
      ["empty": "b704ce", "abc": "ba1c7b", "check": "21cf02", "quick": "a2618c", "long": "65d165"]
    ),
    (
      .crc32,
      [
        "empty": "00000000", "abc": "352441c2", "check": "cbf43926", "quick": "414fa339",
        "long": "9a38da03",
      ]
    ),
    (
      .crc32C,
      [
        "empty": "00000000", "abc": "364b3fb7", "check": "e3069283", "quick": "22620404",
        "long": "9f19ef6a",
      ]
    ),
    (
      .crc32Bzip2,
      [
        "empty": "00000000", "abc": "648cbb73", "check": "fc891918", "quick": "459dee61",
        "long": "49dc4f63",
      ]
    ),
    (
      .crc32Mpeg2,
      [
        "empty": "ffffffff", "abc": "9b73448c", "check": "0376e6e7", "quick": "ba62119e",
        "long": "b623b09c",
      ]
    ),
    (
      .crc32Posix,
      [
        "empty": "ffffffff", "abc": "d3e8c673", "check": "765e7680", "quick": "36b78081",
        "long": "b7cb60fc",
      ]
    ),
    (
      .crc64Xz,
      [
        "empty": "0000000000000000", "abc": "2cd8094a1a277627", "check": "995dc9bbdf1939fa",
        "quick": "5b5eb8c2e54aa1c4", "long": "7610eeea8be8d96c",
      ]
    ),
    (
      .crc64Ecma182,
      [
        "empty": "0000000000000000", "abc": "66501a349a0e0855", "check": "6c40df5f0b497347",
        "quick": "41e05242ffa9883b", "long": "96dc8450ea6c03f0",
      ]
    ),
    (
      .crc64GoIso,
      [
        "empty": "0000000000000000", "abc": "3776c42000000000", "check": "b90956c775a41001",
        "quick": "4ef14e19f4c6e28e", "long": "0eafbc7eeba2f663",
      ]
    ),
    (
      .adler32,
      [
        "empty": "00000001", "abc": "024d0127", "check": "091e01de", "quick": "5bdc0fda",
        "long": "f9d87af8",
      ]
    ),
    (
      .fletcher16,
      ["empty": "0000", "abc": "4c27", "check": "1ede", "quick": "fee8", "long": "4664"]
    ),
    (
      .fletcher32,
      [
        "empty": "00000000", "abc": "c52562c4", "check": "df09d509", "quick": "53cd5b8d",
        "long": "1e1e3232",
      ]
    ),
    (
      .fnv132,
      [
        "empty": "811c9dc5", "abc": "439c2f4b", "check": "24148816", "quick": "e9c86c6e",
        "long": "d0aabc9d",
      ]
    ),
    (
      .fnv1a32,
      [
        "empty": "811c9dc5", "abc": "1a47e90b", "check": "bb86b11c", "quick": "048fff90",
        "long": "1dd9658d",
      ]
    ),
    (
      .fnv164,
      [
        "empty": "cbf29ce484222325", "abc": "d8dcca186bafadcb", "check": "a72ffc362bf916d6",
        "quick": "a8b2f3117de37ace", "long": "dfaa2269cafc097d",
      ]
    ),
    (
      .fnv1a64,
      [
        "empty": "cbf29ce484222325", "abc": "e71fa2190541574b", "check": "06d5573923c6cdfc",
        "quick": "f3f9b7f5e7e47110", "long": "3c835852fbac676d",
      ]
    ),
    (
      .murmur332,
      [
        "empty": "00000000", "abc": "b3dd93fa", "check": "b4fef382", "quick": "2e4ff723",
        "long": "a1e5b608",
      ]
    ),
    (
      .xxHash32,
      [
        "empty": "02cc5d05", "abc": "32d153ff", "check": "937bad67", "quick": "e85ea4de",
        "long": "37e82b86",
      ]
    ),
    (
      .xxHash64,
      [
        "empty": "ef46db3751d8e999", "abc": "44bc2cf5ad770999", "check": "8cb841db40e6ae83",
        "quick": "0b242d361fda71bc", "long": "56e43b712eda4223",
      ]
    ),
    (
      .djb2,
      [
        "empty": "00001505", "abc": "0b885c8b", "check": "35cdbb82", "quick": "34cc38de",
        "long": "d13cc66d",
      ]
    ),
    (
      .sdbm,
      [
        "empty": "00000000", "abc": "3025f862", "check": "68a07035", "quick": "8ca77173",
        "long": "96899d00",
      ]
    ),
    (
      .jenkinsOAAT,
      [
        "empty": "00000000", "abc": "ed131f5b", "check": "c66b58c5", "quick": "519e91f5",
        "long": "caf3d85b",
      ]
    ),
    (
      .md2,
      [
        "empty": "8350e5a3e24c153df2275c9f80692773", "abc": "da853b0d3f88d99b30283a69e6ded6bb",
        "check": "12bd4efdd922b5c8c7b773f26ef4e35f", "quick": "03d85a0d629d2c442e987525319fc471",
        "long": "dd21a412ef3f285fd1f2e70a6c10a702",
      ]
    ),
    (
      .sm3,
      [
        "empty":
          "1ab21d8355cfa17f8e61194831e81a8f22bec8c728fefb747ed035eb5082aa2b",
        "abc":
          "66c7f0f462eeedd9d1f2d46bdc10e4e24167c4875cf2f7a2297da02b8f4ba8e0",
        "check":
          "c7ae0aec3d2f9beb84dc1885aa7a576baa7a07b38060afc64c5600f93a5456b5",
        "quick":
          "5fdfe814b8573ca021983970fc79b2218c9570369b4859684e2e4c3fc76cb8ea",
        "long":
          "f4bedca973227d45c5b822551d2e762d4cfb0e9af70b241452545727b5fb046f",
      ]
    ),
    (
      .whirlpool,
      [
        "empty":
          "19fa61d75522a4669b44e39c1d2e1726c530232130d407f89afee0964997f7a73e83be698b288febcf88e3e03c4f0757ea8964e59b63d93708b138cc42a66eb3",
        "abc":
          "4e2448a4c6f486bb16b6562c73b4020bf3043e3a731bce721ae1b303d97e6d4c7181eebdb6c57e277d0e34957114cbd6c797fc9d95d8b582d225292076d4eef5",
        "check":
          "21d5cb651222c347ea1284c0acf162000b4d3e34766f0d00312e3480f633088822809b6a54ba7edfa17e8fcb5713f8912ee3a218dd98d88c38bbf611b1b1ed2b",
        "quick":
          "b97de512e91e3828b40d2b0fdce9ceb3c4a71f9bea8d88e75c4fa854df36725fd2b52eb6544edcacd6f8beddfea403cb55ae31f03ad62a5ef54e42ee82c3fb35",
        "long":
          "fe24b173807796fdac15ebcaf5769f661695601ffeb64490ec0eecd30bd5b2c3773b36d4edaf3175378b8df114e9496c833ef13606e7ab3d455681e98ecc818f",
      ]
    ),
    // No reference library is available for Tiger here, so only its published Tiger-192 vectors.
    (
      .tiger,
      [
        "empty": "3293ac630c13f0245f92bbb1766e16167a4e58492dde73f3",
        "abc": "2aab1484e8c158f2bfb8c5ff41b57a525129131c957b5f93",
        "quick": "6d12a41e72e644f017b6f0e2f7b44c6285f06dd5d2c5b075",
      ]
    ),
    (
      .streebog256,
      [
        "empty":
          "3f539a213e97c802cc229d474c6aa32a825a360b2a933a949fd925208d9ce1bb",
        "abc":
          "4e2919cf137ed41ec4fb6270c61826cc4fffb660341e0af3688cd0626d23b481",
        "check":
          "84da1066a0205e1446ec4a858ed2314b6233e5790ba5999dde8cd35d5d39f002",
        "quick":
          "3e7dea7f2384b6c5a3d0e24aaa29c05e89ddd762145030ec22c71a6db8b2c1f4",
        "long":
          "e770017612ee6f0bd6dc188268c5dd331e08bad7203153b7a606386d486aebd7",
      ]
    ),
    (
      .streebog512,
      [
        "empty":
          "8e945da209aa869f0455928529bcae4679e9873ab707b55315f56ceb98bef0a7362f715528356ee83cda5f2aac4c6ad2ba3a715c1bcd81cb8e9f90bf4c1c1a8a",
        "abc":
          "28156e28317da7c98f4fe2bed6b542d0dab85bb224445fcedaf75d46e26d7eb8d5997f3e0915dd6b7f0aab08d9c8beb0d8c64bae2ab8b3c8c6bc53b3bf0db728",
        "check":
          "c36fadf5238435a7dda541152c70014a3c2ff0211bba50f15d2279ba13f6f1e4f4108c6b39fc12ca93e73453a95a135bff756312165fc8e4c159dfd6f3a4baf6",
        "quick":
          "d2b793a0bb6cb5904828b5b6dcfb443bb8f33efc06ad09368878ae4cdc8245b97e60802469bed1e7c21a64ff0b179a6a1e0bb74d92965450a0adab69162c00fe",
        "long":
          "15b744f8a6131244ed96d4f351f2df78bab88861e1a3c8743123969f8d924da24faaf6aec9480007c0a7c9eb41fb000d08c769a1de581597f988c0346ce16c58",
      ]
    ),
    // GOST R 34.11-94 with the CryptoPro-A S-box. Only the empty and "abc" digests are published
    // values available in this environment, so the other inputs are not listed.
    (
      .gost341194,
      [
        "empty": "981e5f3ca30c841487830f84fb433e13ac1101569b9c13584ac483234cd656c0",
        "abc": "b285056dbf18d7392d7677369524dd14747459ed8143997e163b2986f92fd42c",
      ]
    ),
    (
      .skein512,
      [
        "empty":
          "bc5b4c50925519c290cc634277ae3d6257212395cba733bbad37a4af0fa06af41fca7903d06564fea7a2d3730dbdb80c1f85562dfcc070334ea4d1d9e72cba7a",
        "abc":
          "8f5dd9ec798152668e35129496b029a960c9a9b88662f7f9482f110b31f9f93893ecfb25c009baad9e46737197d5630379816a886aa05526d3a70df272d96e75",
        "check":
          "3e6859999bae52c0c90cb61865c1e625b3313de272b57cbc22425b23053c1db62e95922afe060694eeeb2a6219d18b2dc47784583a7030d7a1d27736560f70d4",
        "quick":
          "94c2ae036dba8783d0b3f7d6cc111ff810702f5c77707999be7e1c9486ff238a7044de734293147359b4ac7e1d09cd247c351d69826b78dcddd951f0ef912713",
        "long":
          "4869a0b4836d2fc17bfe15cede65755b826547c6cb8978dea690be9048454dae05f67c53504a3fb1f5d1160dbe14022902ffd86d66692699870b37e220a991e2",
      ]
    ),
  ]

  @Test("every algorithm matches its reference on every input")
  func referenceVectors() {
    for (algorithm, table) in Self.vectors {
      for (key, expected) in table {
        let actual = algorithm.hexDigest(of: Self.inputs[key]!)
        #expect(actual == expected, "\(algorithm.rawValue) on \(key)")
      }
    }
  }

  @Test("the table covers every algorithm offered by the app")
  func coversAll() {
    #expect(
      Set(Self.vectors.map { $0.0.rawValue }) == Set(ChecksumAlgorithm.allCases.map { $0.rawValue })
    )
    #expect(ChecksumAlgorithm.allCases.count == 68)
  }
}
