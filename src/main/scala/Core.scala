package WSR32

import chisel3._

class Core extends Module {
    val io = IO(new Bundle {
        val pc          = Output(UInt(32.W))
        val instruction = Input(UInt(32.W))

        val regReadValid = Input(Bool())
        val regReadAddr  = Input(UInt(5.W))
        val regReadData  = Output(UInt(32.W))
    })

    val ifu     = Module(new IFU)
    val idu     = Module(new IDU)
    val exu     = Module(new EXU)
    val ebreak  = Module(new Ebreak)
    val invInst = Module(new InvInst)

    val registerFile = Module(new RegisterFile)

    io.pc              := ifu.io.pc
    idu.io.instruction := io.instruction

    idu.io.rs1Value := registerFile.io.readData1
    idu.io.rs2Value := registerFile.io.readData2

    registerFile.io.readAddr1   := Mux(io.regReadValid, io.regReadAddr, idu.io.rs1)
    registerFile.io.readAddr2   := Mux(io.regReadValid, io.regReadAddr, idu.io.rs2)
    registerFile.io.writeAddr   := idu.io.rd
    registerFile.io.writeData   := exu.io.result
    registerFile.io.writeEnable := idu.io.writeToReg && idu.io.writeEnable
    io.regReadData              := registerFile.io.readData1

    exu.io.a     := idu.io.sourceA
    exu.io.b     := idu.io.sourceB
    exu.io.aluOp := 0.U

    ebreak.io.ebreak := idu.io.ebreak
    ebreak.io.clock  := clock
    ebreak.io.reset  := reset

    invInst.io.invInst := idu.io.invInst
    invInst.io.clock   := clock
    invInst.io.reset   := reset
}
