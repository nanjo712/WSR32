package WSR32

import chisel3._
import chisel3.util.MuxLookup
import chisel3.util.ListLookup

object ALUOperations {
    val Add = 0.U
    val Sub = 1.U
    val Slt = 2.U
    val Ult = 3.U
    val Srl = 4.U
    val Sra = 5.U
    val Sll = 6.U
    val And = 7.U
    val Or  = 8.U
    val Xor = 9.U
}

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
        val aluOp       = Output(UInt(4.W))
        val writeToReg  = Output(Bool())
        val writeEnable = Output(Bool())
        val ebreak      = Output(Bool())
        val invInst     = Output(Bool())
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

    io.rs1         := io.instruction(19, 15)
    io.rs2         := io.instruction(24, 20)
    io.rd          := io.instruction(11, 7)
    io.aluOp       := ALUOperations.Add
    io.writeEnable := false.B
    io.writeToReg  := false.B
    io.ebreak      := io.instruction === "b000000000001_00000_000_00000_1110011".U
    io.invInst     := false.B
    io.sourceA     := 0.U
    io.sourceB     := 0.U

}
