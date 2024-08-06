package WSR32

import chisel3._

class ImmGenerator {
  val io = IO(new Bundle {
    val instruction = Input(UInt(32.W))
    val immI        = Output(UInt(32.W))
    val immS        = Output(UInt(32.W))
    val immB        = Output(UInt(32.W))
    val immU        = Output(UInt(32.W))
    val immJ        = Output(UInt(32.W))
  })

  
}
