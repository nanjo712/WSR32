package WSR32

import chisel3._

class ALU {
    val io = IO(new Bundle {
        val a      = Input(UInt(32.W))
        val b      = Input(UInt(32.W))
        val aluOp  = Input(UInt(4.W))
        val result = Output(UInt(32.W))
    })

    // only add currently
    io.result := a + b
}
