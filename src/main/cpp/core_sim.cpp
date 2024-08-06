#include <cstdint>
#include <iostream>
#include <memory>

#include "VCore.h"
#include "verilated.h"
#include "verilated_vcd_c.h"

uint32_t counter = 0;

void tick(VCore& top, VerilatedVcdC& trace)
{
    top.clock = 0;
    top.eval();
    trace.dump(counter++);

    top.clock = 1;
    top.eval();
    trace.dump(counter++);
}

uint32_t memory[3] = {
    0x02a00093,
    0x00100113,
    0xfd400193,
};

int main(int argc, char** argv)
{
    auto context = std::make_unique<VerilatedContext>();
    context->commandArgs(argc, argv);
    context->traceEverOn(true);

    auto trace = std::make_unique<VerilatedVcdC>();
    auto top = std::make_unique<VCore>(context.get());
    top->trace(trace.get(), 0);
    trace->open("core.vcd");

    top->reset = 1;
    for (int i = 0; i < 10; i++)
    {
        tick(*top, *trace);
    }

    top->reset = 0;

    for (int i = 0; i < 3; i++)
    {
        top->io_instruction = memory[i];
        printf("instruction: %32b\n", memory[i]);
        tick(*top, *trace);
    }

    top->io_regReadValid = 1;
    top->io_regReadAddr = 1;
    top->eval();
    auto x1 = top->io_regReadData;

    top->io_regReadAddr = 2;
    top->eval();
    auto x2 = top->io_regReadData;

    top->io_regReadAddr = 3;
    top->eval();
    auto x3 = top->io_regReadData;

    std::cout << "x1: " << x1 << std::endl;
    std::cout << "x2: " << x2 << std::endl;
    std::cout << "x3: " << (int)x3 << std::endl;
    top->final();
    trace->close();
    return 0;
}