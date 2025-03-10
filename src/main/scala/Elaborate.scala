import chisel3.experimental.BundleLiterals._
import chisel3._
// import chisel3.util._

class Foo extends Module {
    val io = IO(new Bundle {
        val in  = Input(Vec(4, Bool()))
        val idx = Input(UInt(2.W))
        val en  = Input(Bool())
        val out = Output(Bool())
    })

    val x = io.in(io.idx)
    val y = x && io.en
    io.out := y
}

object Elaborate extends App {
    val firtoolOptions = Array(
      "--lowering-options=" + List(
        "disallowLocalVariables",
        "disallowPackedArrays",
        "locationInfoStyle=wrapInAtSquareBracket"
      ).mkString(","),
      "-disable-all-randomization",
      "-strip-debug-info"
    )
    circt.stage.ChiselStage.emitSystemVerilogFile(new WSR32.Core(), args, firtoolOptions)

    // circt.stage.ChiselStage.emitSystemVerilogFile(new (), args, firtoolOptions)
}
