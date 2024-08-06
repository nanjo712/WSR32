package WSR32

import chisel3._

class PCRegister extends Module {
    val io = IO(new Bundle {
        val readData = Output(UInt(32.W))
    })

    val pc = RegInit("h_8000_0000".U(32.W))

    pc := pc + 4.U

    io.readData := pc
}
