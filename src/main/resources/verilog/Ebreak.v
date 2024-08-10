import "DPI-C" function void ebreak_handler();
module Ebreak(
    input wire clock,
    input wire reset,
    input wire ebreak
);
    always @(posedge clock) begin
        if (~reset && ebreak) begin
            ebreak_handler();
        end
    end
endmodule //ebreak

