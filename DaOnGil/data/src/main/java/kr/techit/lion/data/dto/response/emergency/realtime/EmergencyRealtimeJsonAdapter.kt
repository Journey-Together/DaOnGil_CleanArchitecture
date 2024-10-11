package kr.techit.lion.data.dto.response.emergency.realtime

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson

internal class EmergencyRealtimeJsonAdapter {
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
        var rnum: Int? = null
        var hpid: String? = null
        var phpid: String? = null
        var hvidate: String? = null
        var hvec: Int? = null
        var hvoc: Int? = null
        var hvcc: Int? = null
        var hvncc: Int? = null
        var hvccc: Int? = null
        var hvicc: Int? = null
        var hvgc: Int? = null
        var hvdnm: String? = null
        var hvctayn: String? = null
        var hvmriayn: String? = null
        var hvangioayn: String? = null
        var hvventiayn: String? = null
        var hvventisoayn: String? = null
        var hvincuayn: String? = null
        var hvcrrtayn: String? = null
        var hvecmoayn: String? = null
        var hvoxyayn: String? = null
        var hvhypoayn: String? = null
        var hvamyn: String? = null
        var hv1: Int? = null
        var hv2: Int? = null
        var hv3: Int? = null
        var hv4: Int? = null
        var hv5: String? = null
        var hv6: Int? = null
        var hv7: String? = null
        var hv8: Int? = null
        var hv9: Int? = null
        var hv10: String? = null
        var hv11: String? = null
        var hv12: String? = null
        var hv13: Int? = null
        var hv14: Int? = null
        var hv15: Int? = null
        var hv16: Int? = null
        var hv17: Int? = null
        var hv18: Int? = null
        var hv19: Int? = null
        var hv21: Int? = null
        var hv22: Int? = null
        var hv23: Int? = null
        var hv24: Int? = null
        var hv25: Int? = null
        var hv26: Int? = null
        var hv27: Int? = null
        var hv28: Int? = null
        var hv29: Int? = null
        var hv30: Int? = null
        var hv31: Int? = null
        var hv32: Int? = null
        var hv33: Int? = null
        var hv34: Int? = null
        var hv35: Int? = null
        var hv36: Int? = null
        var hv37: Int? = null
        var hv38: Int? = null
        var hv39: Int? = null
        var hv40: Int? = null
        var hv41: Int? = null
        var hv42: String? = null
        var hv43: String? = null
        var dutyName: String? = null
        var dutyTel3: String? = null
        var hvs01: Int? = null
        var hvs02: Int? = null
        var hvs03: Int? = null
        var hvs04: Int? = null
        var hvs05: Int? = null
        var hvs06: Int? = null
        var hvs07: Int? = null
        var hvs08: Int? = null
        var hvs09: Int? = null
        var hvs10: Int? = null
        var hvs11: Int? = null
        var hvs12: Int? = null
        var hvs13: Int? = null
        var hvs14: Int? = null
        var hvs15: Int? = null
        var hvs16: Int? = null
        var hvs17: Int? = null
        var hvs18: Int? = null
        var hvs19: Int? = null
        var hvs20: Int? = null
        var hvs21: Int? = null
        var hvs22: Int? = null
        var hvs23: Int? = null
        var hvs24: Int? = null
        var hvs25: Int? = null
        var hvs26: Int? = null
        var hvs27: Int? = null
        var hvs28: Int? = null
        var hvs29: Int? = null
        var hvs30: Int? = null
        var hvs31: Int? = null
        var hvs32: Int? = null
        var hvs33: Int? = null
        var hvs34: Int? = null
        var hvs35: Int? = null
        var hvs36: Int? = null
        var hvs37: Int? = null
        var hvs38: Int? = null
        var hvs46: Int? = null
        var hvs47: Int? = null
        var hvs48: Int? = null
        var hvs49: Int? = null
        var hvs50: Int? = null
        var hvs51: Int? = null
        var hvs52: Int? = null
        var hvs53: Int? = null
        var hvs54: Int? = null
        var hvs55: Int? = null
        var hvs56: Int? = null
        var hvs57: Int? = null
        var hvs58: Int? = null
        var hvs59: Int? = null

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "rnum" -> rnum = reader.nextInt()
                "hpid" -> hpid = reader.nextString()
                "phpid" -> phpid = reader.nextString()
                "hvidate" -> hvidate = reader.nextString()
                "hvec" -> hvec = reader.nextInt()
                "hvoc" -> hvoc = reader.nextInt()
                "hvcc" -> hvcc = reader.nextInt()
                "hvncc" -> hvncc = reader.nextInt()
                "hvccc" -> hvccc = reader.nextInt()
                "hvicc" -> hvicc = reader.nextInt()
                "hvgc" -> hvgc = reader.nextInt()
                "hvdnm" -> hvdnm = reader.nextString()
                "hvctayn" -> hvctayn = reader.nextString()
                "hvmriayn" -> hvmriayn = reader.nextString()
                "hvangioayn" -> hvangioayn = reader.nextString()
                "hvventiayn" -> hvventiayn = reader.nextString()
                "hvventisoayn" -> hvventisoayn = reader.nextString()
                "hvincuayn" -> hvincuayn = reader.nextString()
                "hvcrrtayn" -> hvcrrtayn = reader.nextString()
                "hvecmoayn" -> hvecmoayn = reader.nextString()
                "hvoxyayn" -> hvoxyayn = reader.nextString()
                "hvhypoayn" -> hvhypoayn = reader.nextString()
                "hvamyn" -> hvamyn = reader.nextString()
                "hv1" -> hv1 = reader.nextInt()
                "hv2" -> hv2 = reader.nextInt()
                "hv3" -> hv3 = reader.nextInt()
                "hv4" -> hv4 = reader.nextInt()
                "hv5" -> hv5 = reader.nextString()
                "hv6" -> hv6 = reader.nextInt()
                "hv7" -> hv7 = reader.nextString()
                "hv8" -> hv8 = reader.nextInt()
                "hv9" -> hv9 = reader.nextInt()
                "hv10" -> hv10 = reader.nextString()
                "hv11" -> hv11 = reader.nextString()
                "hv12" -> hv12 = reader.nextString()
                "hv13" -> hv13 = reader.nextInt()
                "hv14" -> hv14 = reader.nextInt()
                "hv15" -> hv15 = reader.nextInt()
                "hv16" -> hv16 = reader.nextInt()
                "hv17" -> hv17 = reader.nextInt()
                "hv18" -> hv18 = reader.nextInt()
                "hv19" -> hv19 = reader.nextInt()
                "hv21" -> hv21 = reader.nextInt()
                "hv22" -> hv22 = reader.nextInt()
                "hv23" -> hv23 = reader.nextInt()
                "hv24" -> hv24 = reader.nextInt()
                "hv25" -> hv25 = reader.nextInt()
                "hv26" -> hv26 = reader.nextInt()
                "hv27" -> hv27 = reader.nextInt()
                "hv28" -> hv28 = reader.nextInt()
                "hv29" -> hv29 = reader.nextInt()
                "hv30" -> hv30 = reader.nextInt()
                "hv31" -> hv31 = reader.nextInt()
                "hv32" -> hv32 = reader.nextInt()
                "hv33" -> hv33 = reader.nextInt()
                "hv34" -> hv34 = reader.nextInt()
                "hv35" -> hv35 = reader.nextInt()
                "hv36" -> hv36 = reader.nextInt()
                "hv37" -> hv37 = reader.nextInt()
                "hv38" -> hv38 = reader.nextInt()
                "hv39" -> hv39 = reader.nextInt()
                "hv40" -> hv40 = reader.nextInt()
                "hv41" -> hv41 = reader.nextInt()
                "hv42" -> hv42 = reader.nextString()
                "hv43" -> hv43 = reader.nextString()
                "dutyName" -> dutyName = reader.nextString()
                "dutyTel3" -> dutyTel3 = reader.nextString()
                "hvs01" -> hvs01 = reader.nextInt()
                "hvs02" -> hvs02 = reader.nextInt()
                "hvs03" -> hvs03 = reader.nextInt()
                "hvs04" -> hvs04 = reader.nextInt()
                "hvs05" -> hvs05 = reader.nextInt()
                "hvs06" -> hvs06 = reader.nextInt()
                "hvs07" -> hvs07 = reader.nextInt()
                "hvs08" -> hvs08 = reader.nextInt()
                "hvs09" -> hvs09 = reader.nextInt()
                "hvs10" -> hvs10 = reader.nextInt()
                "hvs11" -> hvs11 = reader.nextInt()
                "hvs12" -> hvs12 = reader.nextInt()
                "hvs13" -> hvs13 = reader.nextInt()
                "hvs14" -> hvs14 = reader.nextInt()
                "hvs15" -> hvs15 = reader.nextInt()
                "hvs16" -> hvs16 = reader.nextInt()
                "hvs17" -> hvs17 = reader.nextInt()
                "hvs18" -> hvs18 = reader.nextInt()
                "hvs19" -> hvs19 = reader.nextInt()
                "hvs20" -> hvs20 = reader.nextInt()
                "hvs21" -> hvs21 = reader.nextInt()
                "hvs22" -> hvs22 = reader.nextInt()
                "hvs23" -> hvs23 = reader.nextInt()
                "hvs24" -> hvs24 = reader.nextInt()
                "hvs25" -> hvs25 = reader.nextInt()
                "hvs26" -> hvs26 = reader.nextInt()
                "hvs27" -> hvs27 = reader.nextInt()
                "hvs28" -> hvs28 = reader.nextInt()
                "hvs29" -> hvs29 = reader.nextInt()
                "hvs30" -> hvs30 = reader.nextInt()
                "hvs31" -> hvs31 = reader.nextInt()
                "hvs32" -> hvs32 = reader.nextInt()
                "hvs33" -> hvs33 = reader.nextInt()
                "hvs34" -> hvs34 = reader.nextInt()
                "hvs35" -> hvs35 = reader.nextInt()
                "hvs36" -> hvs36 = reader.nextInt()
                "hvs37" -> hvs37 = reader.nextInt()
                "hvs38" -> hvs38 = reader.nextInt()
                "hvs46" -> hvs46 = reader.nextInt()
                "hvs47" -> hvs47 = reader.nextInt()
                "hvs48" -> hvs48 = reader.nextInt()
                "hvs49" -> hvs49 = reader.nextInt()
                "hvs50" -> hvs50 = reader.nextInt()
                "hvs51" -> hvs51 = reader.nextInt()
                "hvs52" -> hvs52 = reader.nextInt()
                "hvs53" -> hvs53 = reader.nextInt()
                "hvs54" -> hvs54 = reader.nextInt()
                "hvs55" -> hvs55 = reader.nextInt()
                "hvs56" -> hvs56 = reader.nextInt()
                "hvs57" -> hvs57 = reader.nextInt()
                "hvs58" -> hvs58 = reader.nextInt()
                "hvs59" -> hvs59 = reader.nextInt()
                else -> reader.skipValue()
            }
        }
        reader.endObject()
        return Item(
            rnum, hpid, phpid, hvidate, hvec, hvoc, hvcc, hvncc, hvccc, hvicc, hvgc, hvdnm, hvctayn, hvmriayn,
            hvangioayn, hvventiayn, hvventisoayn, hvincuayn, hvcrrtayn, hvecmoayn, hvoxyayn, hvhypoayn, hvamyn,
            hv1, hv2, hv3, hv4, hv5, hv6, hv7, hv8, hv9, hv10, hv11, hv12, hv13, hv14, hv15, hv16, hv17, hv18,
            hv19, hv21, hv22, hv23, hv24, hv25, hv26, hv27, hv28, hv29, hv30, hv31, hv32, hv33, hv34, hv35, hv36,
            hv37, hv38, hv39, hv40, hv41, hv42, hv43, dutyName, dutyTel3, hvs01, hvs02, hvs03, hvs04, hvs05,
            hvs06, hvs07, hvs08, hvs09, hvs10, hvs11, hvs12, hvs13, hvs14, hvs15, hvs16, hvs17, hvs18, hvs19,
            hvs20, hvs21, hvs22, hvs23, hvs24, hvs25, hvs26, hvs27, hvs28, hvs29, hvs30, hvs31, hvs32, hvs33,
            hvs34, hvs35, hvs36, hvs37, hvs38, hvs46, hvs47, hvs48, hvs49, hvs50, hvs51, hvs52, hvs53, hvs54, hvs55, hvs56, hvs57, hvs58, hvs59
        )
    }

    private fun writeItem(writer: JsonWriter, item: Item) {
        writer.beginObject()
        writer.name("rnum").value(item.rnum)
        writer.name("hpid").value(item.hpid)
        writer.name("phpid").value(item.phpid)
        writer.name("hvidate").value(item.hvidate)
        writer.name("hvec").value(item.hvec)
        writer.name("hvoc").value(item.hvoc)
        writer.name("hvcc").value(item.hvcc)
        writer.name("hvncc").value(item.hvncc)
        writer.name("hvccc").value(item.hvccc)
        writer.name("hvicc").value(item.hvicc)
        writer.name("hvgc").value(item.hvgc)
        writer.name("hvdnm").value(item.hvdnm)
        writer.name("hvctayn").value(item.hvctayn)
        writer.name("hvmriayn").value(item.hvmriayn)
        writer.name("hvangioayn").value(item.hvangioayn)
        writer.name("hvventiayn").value(item.hvventiayn)
        writer.name("hvventisoayn").value(item.hvventisoayn)
        writer.name("hvincuayn").value(item.hvincuayn)
        writer.name("hvcrrtayn").value(item.hvcrrtayn)
        writer.name("hvecmoayn").value(item.hvecmoayn)
        writer.name("hvoxyayn").value(item.hvoxyayn)
        writer.name("hvhypoayn").value(item.hvhypoayn)
        writer.name("hvamyn").value(item.hvamyn)
        writer.name("hv1").value(item.hv1)
        writer.name("hv2").value(item.hv2)
        writer.name("hv3").value(item.hv3)
        writer.name("hv4").value(item.hv4)
        writer.name("hv5").value(item.hv5)
        writer.name("hv6").value(item.hv6)
        writer.name("hv7").value(item.hv7)
        writer.name("hv8").value(item.hv8)
        writer.name("hv9").value(item.hv9)
        writer.name("hv10").value(item.hv10)
        writer.name("hv11").value(item.hv11)
        writer.name("hv12").value(item.hv12)
        writer.name("hv13").value(item.hv13)
        writer.name("hv14").value(item.hv14)
        writer.name("hv15").value(item.hv15)
        writer.name("hv16").value(item.hv16)
        writer.name("hv17").value(item.hv17)
        writer.name("hv18").value(item.hv18)
        writer.name("hv19").value(item.hv19)
        writer.name("hv21").value(item.hv21)
        writer.name("hv22").value(item.hv22)
        writer.name("hv23").value(item.hv23)
        writer.name("hv24").value(item.hv24)
        writer.name("hv25").value(item.hv25)
        writer.name("hv26").value(item.hv26)
        writer.name("hv27").value(item.hv27)
        writer.name("hv28").value(item.hv28)
        writer.name("hv29").value(item.hv29)
        writer.name("hv30").value(item.hv30)
        writer.name("hv31").value(item.hv31)
        writer.name("hv32").value(item.hv32)
        writer.name("hv33").value(item.hv33)
        writer.name("hv34").value(item.hv34)
        writer.name("hv35").value(item.hv35)
        writer.name("hv36").value(item.hv36)
        writer.name("hv37").value(item.hv37)
        writer.name("hv38").value(item.hv38)
        writer.name("hv46").value(item.hvs46)
        writer.name("hv47").value(item.hvs47)
        writer.name("hv48").value(item.hvs48)
        writer.name("hv49").value(item.hvs49)
        writer.name("hv50").value(item.hvs50)
        writer.name("hv51").value(item.hvs51)
        writer.name("hv52").value(item.hvs52)
        writer.name("hv53").value(item.hvs53)
        writer.name("hv54").value(item.hvs54)
        writer.name("hv55").value(item.hvs55)
        writer.name("hv56").value(item.hvs56)
        writer.name("hv57").value(item.hvs57)
        writer.name("hv58").value(item.hvs58)
        writer.name("hv59").value(item.hvs59)
        writer.name("dutyName").value(item.dutyName)
        writer.name("dutyTel3").value(item.dutyTel3)
        writer.endObject()
    }
}