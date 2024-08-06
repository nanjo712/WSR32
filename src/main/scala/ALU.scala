package WSR32

import chisel3._

class ALU extends Module {
    val io = IO(new Bundle {
        val a      = Input(UInt(32.W))
        val b      = Input(UInt(32.W))
        val aluOp  = Input(UInt(4.W))
        val result = Output(UInt(32.W))
    })

    // only add currently
    io.result := io.a + io.b
    // TODO: implement other operations
}
