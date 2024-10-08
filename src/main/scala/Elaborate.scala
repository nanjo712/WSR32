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
}
