package WSR32

import chisel3._

class PCRegister {
  val io = IO(new Bundle {
    val writeData   = Input(UInt(32.W))
    val writeEnable = Input(Bool())
    val readData    = Output(UInt(32.W))
  })

  val pc = RegInit("h_8000_0000".U(32.W))

  pc := Mux(io.writeEnable, io.writeData, pc)

  io.readData := pc
}
