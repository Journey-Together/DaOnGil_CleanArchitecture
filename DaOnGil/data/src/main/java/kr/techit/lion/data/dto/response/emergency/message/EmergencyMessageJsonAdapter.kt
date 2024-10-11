package kr.techit.lion.data.dto.response.emergency.message

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson

internal class EmergencyMessageJsonAdapter {
    @FromJson
    fun fromJson(reader: JsonReader): Items {
        val items = mutableListOf<Item>()

        when (reader.peek()) {
            JsonReader.Token.BEGIN_OBJECT -> {
                reader.beginObject()
                while (reader.hasNext()) {
                    if (reader.nextName() == "item") {
                        when (reader.peek()) {
                            JsonReader.Token.BEGIN_OBJECT -> {
                                items.add(readItem(reader))
                            }
                            JsonReader.Token.BEGIN_ARRAY -> {
                                reader.beginArray()
                                while (reader.hasNext()) {
                                    items.add(readItem(reader))
                                }
                                reader.endArray()
                            }
                            else -> reader.skipValue()
                        }
                    } else {
                        reader.skipValue()
                    }
                }
                reader.endObject()
            }
            JsonReader.Token.BEGIN_ARRAY -> {
                reader.beginArray()
                while (reader.hasNext()) {
                    items.add(readItem(reader))
                }
                reader.endArray()
            }
            JsonReader.Token.STRING -> {
                reader.nextString() // 빈 문자열 처리
            }
            else -> reader.skipValue()
        }

        return Items(items)
    }

    @ToJson
    fun toJson(writer: JsonWriter, value: Items?) {
        writer.beginObject()
        writer.name("item")
        writer.beginArray()
        value?.item?.forEach { item ->
            writeItem(writer, item)
        }
        writer.endArray()
        writer.endObject()
    }

    private fun readItem(reader: JsonReader): Item {
        var dutyAddr: String? = null
        var dutyName: String? = null
        var emcOrgCod: String? = null
        var hpid: String? = null
        var rnum: Int? = null
        var symBlkEndDtm: Long? = null
        var symBlkMsg: String? = null
        var symBlkMsgTyp: String? = null
        var symBlkSttDtm: Long? = null
        var symOutDspMth: String? = null
        var symOutDspYon: String? = null
        var symTypCod: String? = null
        var symTypCodMag: String? = null

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "dutyAddr" -> dutyAddr = reader.nextString()
                "dutyName" -> dutyName = reader.nextString()
                "emcOrgCod" -> emcOrgCod = reader.nextString()
                "hpid" -> hpid = reader.nextString()
                "rnum" -> rnum = reader.nextInt()
                "symBlkEndDtm" -> symBlkEndDtm = reader.nextLong()
                "symBlkMsg" -> symBlkMsg = reader.nextString()
                "symBlkMsgTyp" -> symBlkMsgTyp = reader.nextString()
                "symBlkSttDtm" -> symBlkSttDtm = reader.nextLong()
                "symOutDspMth" -> symOutDspMth = reader.nextString()
                "symOutDspYon" -> symOutDspYon = reader.nextString()
                "symTypCod" -> symTypCod = reader.nextString()
                "symTypCodMag" -> symTypCodMag = reader.nextString()
                else -> reader.skipValue()
            }
        }
        reader.endObject()
        return Item(
            dutyAddr ?: "",
            dutyName ?: "",
            emcOrgCod ?: "",
            hpid ?: "",
            rnum ?: 0,
            symBlkEndDtm ?: 0L,
            symBlkMsg ?: "",
            symBlkMsgTyp ?: "",
            symBlkSttDtm ?: 0L,
            symOutDspMth ?: "",
            symOutDspYon ?: "",
            symTypCod ?: "",
            symTypCodMag ?: ""
        )
    }

    private fun writeItem(writer: JsonWriter, item: Item) {
        writer.beginObject()
        writer.name("dutyAddr").value(item.dutyAddr)
        writer.name("dutyName").value(item.dutyName)
        writer.name("emcOrgCod").value(item.emcOrgCod)
        writer.name("hpid").value(item.hpid)
        writer.name("rnum").value(item.rnum)
        writer.name("symBlkEndDtm").value(item.symBlkEndDtm)
        writer.name("symBlkMsg").value(item.symBlkMsg)
        writer.name("symBlkMsgTyp").value(item.symBlkMsgTyp)
        writer.name("symBlkSttDtm").value(item.symBlkSttDtm)
        writer.name("symOutDspMth").value(item.symOutDspMth)
        writer.name("symOutDspYon").value(item.symOutDspYon)
        writer.name("symTypCod").value(item.symTypCod)
        writer.name("symTypCodMag").value(item.symTypCodMag)
        writer.endObject()
    }
}