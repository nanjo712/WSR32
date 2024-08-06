package WSR32

import chisel3._

class EXU extends Module {
    val io = IO(new Bundle {
        val a      = Input(UInt(32.W))
        val b      = Input(UInt(32.W))
        val aluOp  = Input(UInt(4.W))
        val result = Output(UInt(32.W))
    })

    val alu = Module(new ALU)

    alu.io.a     := io.a
    alu.io.b     := io.b
    alu.io.aluOp := io.aluOp

    io.result := alu.io.result
}
