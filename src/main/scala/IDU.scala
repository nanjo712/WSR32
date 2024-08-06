package WSR32

import chisel3._

class IDU extends Module {
    val io = IO(new Bundle {
        val instruction = Input(UInt(32.W))

        val rs1      = Output(UInt(5.W))
        val rs1Value = Input(UInt(32.W))

        val rs2      = Output(UInt(5.W))
        val rs2Value = Input(UInt(32.W))

        val rd          = Output(UInt(5.W))
        val sourceA     = Output(UInt(32.W))
        val sourceB     = Output(UInt(32.W))
        val writeToReg  = Output(Bool())
        val writeEnable = Output(Bool())
    })

    val immGenerator = Module(new ImmGenerator)
    immGenerator.io.instruction := io.instruction

    val opcode = io.instruction(6, 0)
    val funct3 = io.instruction(14, 12)
    val funct7 = io.instruction(31, 25)
    val immI   = immGenerator.io.immI
    val immS   = immGenerator.io.immS
    val immB   = immGenerator.io.immB
    val immU   = immGenerator.io.immU
    val immJ   = immGenerator.io.immJ

    io.rs1 := io.instruction(19, 15)
    io.rs2 := io.instruction(24, 20)
    io.rd  := io.instruction(11, 7)

    io.writeEnable := false.B
    io.writeToReg  := false.B
    io.sourceA     := 0.U
    io.sourceB     := 0.U

    when(opcode === "b0010011".U && funct3 === "b000".U) {
        io.writeEnable := true.B
        io.writeToReg  := true.B
        io.sourceA     := io.rs1Value
        io.sourceB     := immI
    }
}
