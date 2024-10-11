package kr.techit.lion.data.dto.response.aed

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson

internal class AedJsonAdapter {
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
        var buildAddress: String? = null
        var buildPlace: String? = null
        var clerkTel: String? = null
        var friEndTme: Int? = null
        var friSttTme: String? = null
        var holEndTme: Int? = null
        var holSttTme: String? = null
        var manager: String? = null
        var managerTel: String? = null
        var mfg: String? = null
        var model: String? = null
        var monEndTme: Int? = null
        var monSttTme: String? = null
        var org: String? = null
        var satEndTme: Int? = null
        var satSttTme: String? = null
        var sunEndTme: Int? = null
        var sunFifYon: String? = null
        var sunFrtYon: String? = null
        var sunFurYon: String? = null
        var sunScdYon: String? = null
        var sunSttTme: String? = null
        var sunThiYon: String? = null
        var thuEndTme: Int? = null
        var thuSttTme: String? = null
        var tueEndTme: Int? = null
        var tueSttTme: String? = null
        var wedEndTme: Int? = null
        var wedSttTme: String? = null
        var wgs84Lat: Double? = null
        var wgs84Lon: Double? = null
        var zipcode1: Int? = null
        var zipcode2: Int? = null
        var rnum: Int? = null

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "buildAddress" -> buildAddress = reader.nextString()
                "buildPlace" -> buildPlace = reader.nextString()
                "clerkTel" -> clerkTel = reader.nextString()
                "friEndTme" -> friEndTme = readIntOrString(reader)
                "friSttTme" -> friSttTme = reader.nextString()
                "holEndTme" -> holEndTme = readIntOrString(reader)
                "holSttTme" -> holSttTme = reader.nextString()
                "manager" -> manager = reader.nextString()
                "managerTel" -> managerTel = reader.nextString()
                "mfg" -> mfg = reader.nextString()
                "model" -> model = reader.nextString()
                "monEndTme" -> monEndTme = readIntOrString(reader)
                "monSttTme" -> monSttTme = reader.nextString()
                "org" -> org = reader.nextString()
                "satEndTme" -> satEndTme = readIntOrString(reader)
                "satSttTme" -> satSttTme = reader.nextString()
                "sunEndTme" -> sunEndTme = readIntOrString(reader)
                "sunFifYon" -> sunFifYon = reader.nextString()
                "sunFrtYon" -> sunFrtYon = reader.nextString()
                "sunFurYon" -> sunFurYon = reader.nextString()
                "sunScdYon" -> sunScdYon = reader.nextString()
                "sunSttTme" -> sunSttTme = reader.nextString()
                "sunThiYon" -> sunThiYon = reader.nextString()
                "thuEndTme" -> thuEndTme = readIntOrString(reader)
                "thuSttTme" -> thuSttTme = reader.nextString()
                "tueEndTme" -> tueEndTme = readIntOrString(reader)
                "tueSttTme" -> tueSttTme = reader.nextString()
                "wedEndTme" -> wedEndTme = readIntOrString(reader)
                "wedSttTme" -> wedSttTme = reader.nextString()
                "wgs84Lat" -> wgs84Lat = reader.nextDouble()
                "wgs84Lon" -> wgs84Lon = reader.nextDouble()
                "zipcode1" -> zipcode1 = reader.nextInt()
                "zipcode2" -> zipcode2 = reader.nextInt()
                "rnum" -> rnum = reader.nextInt()
                else -> reader.skipValue()
            }
        }
        reader.endObject()
        return Item(
            buildAddress, buildPlace, clerkTel, friEndTme, friSttTme, holEndTme, holSttTme,
            manager, managerTel, mfg, model, monEndTme, monSttTme, org, satEndTme, satSttTme,
            sunEndTme, sunFifYon, sunFrtYon, sunFurYon, sunScdYon, sunSttTme, sunThiYon, thuEndTme,
            thuSttTme, tueEndTme, tueSttTme, wedEndTme, wedSttTme, wgs84Lat, wgs84Lon, zipcode1, zipcode2, rnum
        )
    }

    private fun readIntOrString(reader: JsonReader): Int? {
        return when (reader.peek()) {
            JsonReader.Token.NUMBER -> reader.nextInt()
            JsonReader.Token.STRING -> reader.nextString().toIntOrNull()
            else -> {
                reader.skipValue()
                null
            }
        }
    }

    private fun writeItem(writer: JsonWriter, item: Item) {
        writer.beginObject()
        writer.name("buildAddress").value(item.buildAddress)
        writer.name("buildPlace").value(item.buildPlace)
        writer.name("clerkTel").value(item.clerkTel)
        writer.name("friEndTme").value(item.friEndTme)
        writer.name("friSttTme").value(item.friSttTme)
        writer.name("holEndTme").value(item.holEndTme)
        writer.name("holSttTme").value(item.holSttTme)
        writer.name("manager").value(item.manager)
        writer.name("managerTel").value(item.managerTel)
        writer.name("mfg").value(item.mfg)
        writer.name("model").value(item.model)
        writer.name("monEndTme").value(item.monEndTme)
        writer.name("monSttTme").value(item.monSttTme)
        writer.name("org").value(item.org)
        writer.name("satEndTme").value(item.satEndTme)
        writer.name("satSttTme").value(item.satSttTme)
        writer.name("sunEndTme").value(item.sunEndTme)
        writer.name("sunFifYon").value(item.sunFifYon)
        writer.name("sunFrtYon").value(item.sunFrtYon)
        writer.name("sunFurYon").value(item.sunFurYon)
        writer.name("sunScdYon").value(item.sunScdYon)
        writer.name("sunSttTme").value(item.sunSttTme)
        writer.name("sunThiYon").value(item.sunThiYon)
        writer.name("thuEndTme").value(item.thuEndTme)
        writer.name("thuSttTme").value(item.thuSttTme)
        writer.name("tueEndTme").value(item.tueEndTme)
        writer.name("tueSttTme").value(item.tueSttTme)
        writer.name("wedEndTme").value(item.wedEndTme)
        writer.name("wedSttTme").value(item.wedSttTme)
        writer.name("wgs84Lat").value(item.wgs84Lat)
        writer.name("wgs84Lon").value(item.wgs84Lon)
        writer.name("zipcode1").value(item.zipcode1)
        writer.name("zipcode2").value(item.zipcode2)
        writer.name("rnum").value(item.rnum)
        writer.endObject()
    }
}