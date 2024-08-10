import "DPI-C" function void invalid_instruction_handler();
module InvInst(
    input wire clock,
    input wire reset,
    input wire invInst
);
    always @(posedge clock) begin
        if (~reset && invInst) begin
            invalid_instruction_handler();
        end
    end
endmodule //InvInst
