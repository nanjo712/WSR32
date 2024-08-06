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

    io.immI = Cat(Fill(20, io.instruction(31)), io.instruction(31, 20))
    io.immS = Cat(Fill(20, io.instruction(31)), io.instruction(31, 25), io.instruction(11, 7))
    io.immB = Cat(
      Fill(19, io.instruction(31)),
      io.instruction(31),
      io.instruction(7),
      io.instruction(30, 25),
      io.instruction(11, 8),
      0.U
    )
    io.immU = Cat(io.instruction(31, 12), Fill(12, 0.U))
    io.immJ = Cat(
      Fill(11, io.instruction(31)),
      io.instruction(19, 12),
      io.instruction(20),
      io.instruction(30, 21),
      0.U
    )
}
