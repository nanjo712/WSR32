package WSR32

import chisel3._

class IFU extends Module {
    val io = IO(new Bundle {
        val pc = Output(UInt(32.W))
    })

    val pcRegister = Module(new PCRegister)

    io.pc := pcRegister.io.readData
}
