package info.nightscout.androidaps.plugins.pump.insight.app_layer.history.history_events

import info.nightscout.androidaps.plugins.pump.insight.utils.ByteBuf

class SniffingDoneEvent : HistoryEvent() {

    var amount = 0.0
        private set

    override fun parse(byteBuf: ByteBuf) {
        amount = byteBuf.readUInt16Decimal()
    }
}