(
SuperDirt.postBadValues = false;

s.options.numBuffers = 1024 * 16;
s.options.memSize = 8192 * 16;
s.options.maxNodes = 1024 * 64;
s.options.numOutputBusChannels = 2;
s.options.numInputBusChannels = 0;

s.waitForBoot {
	~dirt = SuperDirt(2, s);

	s.sync;
	~dirt.start(57120, [0]);

	MIDIClient.init;

	~rytmOut = MIDIOut.newByName("Elektron Analog Rytm", "Analog Rytm out 1");
	~rytmOut.latency = 0;
	~dirt.soundLibrary.addMIDI(\rytm, ~rytmOut);

	~harmorOut = MIDIOut.newByName("IAC Driver", "Bus 1");
	~harmorOut.latency = 0;
	~dirt.soundLibrary.addMIDI(\harmor, ~harmorOut);

	~midiOut = MIDIOut.newByName("Scarlett 2i4 USB", "Scarlett 2i4 USB");
	~midiOut.latency = 0;
	~dirt.soundLibrary.addMIDI(\midi, ~midiOut);

};
s.latency = 0;
);



// Evaluate the block below to start the mapping MIDI -> OSC.
(
var on, off, cc;
var osc;

osc = NetAddr.new("127.0.0.1", 6010);

MIDIClient.init;
MIDIIn.connectAll;

MIDIIn.connect(inport: 0, device: 5);

// on = MIDIFunc.noteOn({ |val, num, chan, src|
// 	osc.sendMsg("/ctrl", num.asString, val/127);
// });

// off = MIDIFunc.noteOff({ |val, num, chan, src|
// 	osc.sendMsg("/ctrl", num.asString, 0);
// });

cc = MIDIFunc.cc({ |val, num, chan, src|
	osc.sendMsg("/ctrl", num.asString, val/127);
});

if (~stopMidiToOsc != nil, {
	~stopMidiToOsc.value;
});

~stopMidiToOsc = {
	on.free;
	off.free;
	cc.free;
};
)

// Evaluate the line below to stop it.
~stopMidiToOsc.value;