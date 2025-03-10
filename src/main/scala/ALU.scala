package WSR32

import chisel3._
import chisel3.util._

/*
    ALU Operations
    Add 0000
    Sub 0001
    Slt 0010
    Ult 0011
    Srl 0100
    Sra 0101
    Sll 0110
    And 0111
    Or  1000
    Xor 1001
 */

class ALU extends Module {
    val io = IO(new Bundle {
        val a      = Input(UInt(32.W))
        val b      = Input(UInt(32.W))
        val aluOp  = Input(UInt(4.W))
        val result = Output(UInt(32.W))
    })

    val sub = io.aluOp(0) | io.aluOp(1)

    // Adder
    val t_A      = io.a
    val t_B      = io.b ^ Fill(32, sub)
    val result   = t_A + t_B + sub.asUInt
    val overflow = (t_A(31) & t_B(31) & !result(31)) | (!t_A(31) & !t_B(31) & result(31))

    val table = Seq(
      "b0000".U -> result.asUInt,
      "b0001".U -> result.asUInt,
      "b0010".U -> (result(31) ^ overflow).asUInt,
      "b0011".U -> overflow.asUInt,
      "b0100".U -> (io.a >> io.b(4, 0)).asUInt,
      "b0101".U -> (io.a.asSInt >> io.b(4, 0)).asUInt,
      "b0110".U -> (io.a << io.b(4, 0)),
      "b0111".U -> (io.a & io.b).asUInt,
      "b1000".U -> (io.a | io.b).asUInt,
      "b1001".U -> (io.a ^ io.b).asUInt
    )

    io.result := MuxLookup(io.aluOp, 0.U) { table }

}
