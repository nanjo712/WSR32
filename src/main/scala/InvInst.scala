package WSR32

import chisel3._
import chisel3.util.HasBlackBoxPath

class InvInst extends BlackBox with HasBlackBoxPath {
    val io = IO(new Bundle {
        val clock   = Input(Clock())
        val reset   = Input(Reset())
        val invInst = Input(Bool())
    })
    addPath("src/main/resources/verilog/InvInst.v")
}
