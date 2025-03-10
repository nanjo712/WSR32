package WSR32

import chisel3._

class IFU extends Module {
    val io = IO(new Bundle {
        val pc          = Input(UInt(32.W))
        val instruction = Output(UInt(32.W))
    })

    val memInterface = IO(Flipped(new MemoryInterface))

    memInterface.addr := io.pc
    memInterface.read := true.B
    io.instruction    := memInterface.dataOut
}
