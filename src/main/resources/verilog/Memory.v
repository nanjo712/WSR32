import "DPI-C" function void write_memory(int address, int data, byte mask);
import "DPI-C" function int read_memory(int address);

module Memory(
    input wire write,
    input wire read,
    input wire [31:0] address,
    input wire [31:0] dataIn,
    input wire [7:0] mask,
    output reg [31:0] dataOut
);
    always @(*) begin
        if (write) begin
            write_memory(address, dataIn, mask);
        end
        if (read) begin
            dataOut = read_memory(address);
        end
        else begin
            dataOut = 0;
        end
    end
endmodule
