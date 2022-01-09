package org.netherald.minejs.common

import com.eclipsesource.v8.JavaCallback
import com.eclipsesource.v8.V8
import com.eclipsesource.v8.V8Array
import com.eclipsesource.v8.V8Object
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Types
import kotlin.jvm.internal.Reflection

object SqlFeature {

    var connection: Connection? = null

    fun initV8(v8Object: V8Object): V8Object {
        val result = v8Object
        result.registerJavaMethod({ _, parameters ->
            if(parameters.length() == 5) {
                init(parameters[0] as String, parameters[1] as String, parameters[2] as String, parameters[3] as String, parameters[4] as String)
            } else if(parameters.length() == 4) {
                init(parameters[0] as String, parameters[1] as String, parameters[2] as String, parameters[3] as String)
            } else {
                init(parameters[0] as String, parameters[1] as String, parameters[2] as String)
            }
        }, "connect")

        result.registerJavaMethod(JavaCallback { _, parameters ->
            return@JavaCallback V8Object(v8Object.runtime).apply(createQuery(parameters[0] as String, result.runtime))
        }, "createQuery")
        return result
    }

    fun init(addr: String, db: String, user: String, pass: String? = null, timezone: String = "Asia/Seoul") {

        if(connection != null) return

        Class.forName("com.mysql.cj.jdbc.Driver")
        connection = DriverManager.getConnection("jdbc:mysql://$addr/$db?serverTimezone=$timezone", user, pass ?: "")
    }

    fun createQuery(query: String, runtime: V8): V8Object.() -> Unit {
        return {
            val statement = connection!!.prepareStatement(query)

            registerJavaMethod({ receiver, parameters ->
                val i = parameters[0] as Int
                val v = parameters[1]

                if(v is Int)
                    statement.setInt(i, v)
                else if(v is Double)
                    statement.setDouble(i, v)
                else if(v is String)
                    statement.setString(i, v)
                else if(v is Boolean)
                    statement.setBoolean(i, v)
                else {
                    if(v is V8Object) {
                        for (key in v.keys) {
                            println("Debug: ${v.get(key)}")
                        }
                    }
                    throw IllegalArgumentException("Not int, double, string, or boolean!")
                }
            }, "set")

            registerJavaMethod(JavaCallback { receiver, parameters ->
                val resultExecute = statement.execute()
                if(resultExecute) {
                    val rs = statement.executeQuery()
                    val rsMeta = rs.metaData
                    val array = V8Array(runtime)

                    while(rs.next()) {
                        val rowObject = V8Object(runtime)

                        for(i in 1..rsMeta.columnCount) {
                            val column = rsMeta.getColumnName(i)
                            when(rsMeta.getColumnTypeName(i)) {
                                "BIT" -> rowObject.add(column, rs.getBoolean(column))
                                "CHAR" -> rowObject.add(column, rs.getString(column))
                                "VARCHAR" -> rowObject.add(column, rs.getString(column))
                                "FLOAT" -> rowObject.add(column, rs.getFloat(column).toDouble())
                                "DOUBLE" -> rowObject.add(column, rs.getDouble(column))
                                "TINYINT" -> rowObject.add(column, rs.getInt(column))
                                "SMALLINT" -> rowObject.add(column, rs.getInt(column))
                                "SMALLINT UNSIGNED" -> rowObject.add(column, rs.getInt(column))
                                "MEDIUMINT" -> rowObject.add(column, rs.getInt(column))
                                "MEDIUMINT UNSIGNED" -> rowObject.add(column, rs.getInt(column))
                                "INTEGER" -> rowObject.add(column, rs.getInt(column))
                                "INTEGER UNSIGNED" -> rowObject.add(column, rs.getLong(column).toDouble())
                                "INT" -> rowObject.add(column, rs.getInt(column))
                                "INT UNSIGNED" -> rowObject.add(column, rs.getLong(column).toDouble())
                                "BIGINT" -> rowObject.add(column, rs.getLong(column).toDouble())
                                "BIGINT UNSIGNED" -> rowObject.add(column, rs.getBigDecimal(column).toDouble())
                            }
                            //rowObject[rsMeta.getColumnName(i)] = rs.get
                        }

                        array.push(rowObject)
                    }

                    return@JavaCallback array
                } else {
                    return@JavaCallback true
                }
            }, "execute")
        }
    }

    fun close() {
        if(connection == null) return

        connection!!.close()

        connection = null
    }

}