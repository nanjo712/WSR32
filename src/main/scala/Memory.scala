package WSR32

import chisel3._
import chisel3.util.HasBlackBoxPath

class MemoryInterface extends Bundle {
    val write   = Input(Bool())
    val read    = Input(Bool())
    val addr    = Input(UInt(32.W))
    val dataIn  = Input(UInt(32.W))
    val mask    = Input(UInt(8.W))
    val dataOut = Output(UInt(32.W))
}

class Memory extends BlackBox with HasBlackBoxPath {
    val io = IO(new MemoryInterface)
    addPath("src/main/resources/verilog/Memory.v")
}
