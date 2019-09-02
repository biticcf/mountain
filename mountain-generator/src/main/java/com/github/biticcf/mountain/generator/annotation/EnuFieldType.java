/**
 * 
 */
package com.github.biticcf.mountain.generator.annotation;

/**
 * author: Daniel.Cao
 * date:   2019年3月12日
 * time:   下午5:14:07
 * +Java、MySql、Mybatis数据类型对应枚举
 */
public enum EnuFieldType {
	// 整型
	BIT("java.lang.Boolean", "bit", "BIT"),        // bit(m),BIT数据类型可用来保存位字段值。BIT(M)类型允许存储M位值。M范围为1~64，默认为1。
	TINYINT("java.lang.Byte", "tinyint", "TINYINT"),    // tinyint(m),1个字节  范围(-128~127)
	SMALLINT("java.lang.Short", "smallint", "SMALLINT"),   // smallint(m),2个字节  范围(-32768~32767)
	MEDIUMINT("java.lang.Integer", "mediumint", "INTEGER"),  // mediumint(m),3个字节  范围(-8388608~8388607)
	INTEGER("java.lang.Integer", "integer", "INTEGER"),    // integer(m),4个字节  范围(-2147483648~2147483647)
	BIGINT("java.lang.Long", "bigint", "BIGINT"),     // bigint(m),8个字节  范围(+-9.22*10的18次方)
	// 浮点型
	FLOAT("java.lang.Float", "float", "FLOAT"),      // float(m,d),单精度浮点型    8位精度(4字节)     m总个数，d小数位 
	DOUBLE("java.lang.Double", "double", "DOUBLE"),     // double(m,d),双精度浮点型    16位精度(8字节)    m总个数，d小数位
	REAL("java.math.BigDecimal", "real", "REAL"),    // real(m,d) REAL就是DOUBLE，如果SQL服务器模式包括REAL_AS_FLOAT选项，REAL是FLOAT的同义词而不是DOUBLE的同义词
	// 定点数(numeric和decimal同义)
	DECIMAL("java.math.BigDecimal", "decimal", "DECIMAL"),    // decimal(m,d) 参数 m<65 是总个数，d<30 且 d<m 是小数位
	NUMERIC("java.math.BigDecimal", "numeric", "NUMERIC"),    // numeric(m,d) 参数 m<65 是总个数，d<30 且 d<m 是小数位
	// 字符串
	CHAR("java.lang.String", "char", "CHAR"),       // char(n),固定长度，最多255个字符 
	VARCHAR("java.lang.String", "varchar", "VARCHAR"),    // varchar(n),可变长度，最多65535个字符 
	TINYTEXT("java.lang.String", "tinytext", "VARCHAR"),   // tinytext,可变长度，最多255个字符 
	TEXT("java.lang.String", "text", "VARCHAR"),       // text,可变长度，最多65535个字符
	MEDIUMTEXT("java.lang.String", "mediumtext", "LONGVARCHAR"), // mediumtext,可变长度，最多16777215字符 
	LONGTEXT("java.lang.String", "longtext", "LONGVARCHAR"),   // 可变长度，最多4294967295字符 
	// 二进制数据
	BINARY("java.lang.Byte[]", "binary", "BINARY"),    // binary(n),固定长度，最多255个字节
	VARBINARY("java.lang.Byte[]", "varbinary", "VARBINARY"), // varbinary(n),可变长度，最多65535个字节 
	TINYBLOB("java.lang.Byte[]", "tinyblob", "BLOB"),   // tinyblob,最多255字节，不超过 255 个字符的二进制字符串
	BLOB("java.lang.Byte[]", "blob", "BLOB"),       // blob,最多65535字节，二进制形式的长文本数据
	MEDIUMBLOB("java.lang.Byte[]", "mediumblob", "LONGVARBINARY"), // mediumblob,最多16777215字节，二进制形式的中等长度文本数据 
	LONGBLOB("java.lang.Byte[]", "longblob", "LONGVARBINARY"),   // longblob,最多4294967295字节，二进制形式的极大文本数据 
	// 日期时间类型
	DATE("java.util.Date", "date", "TIMESTAMP"),       // date,3字节，日期 '2008-12-2'
	TIME("java.util.Date", "time", "TIMESTAMP"),       // time,3字节，时间 '12:25:36'
	YEAR("java.util.Integer", "year", "INTEGER"),       // year,1字节，1901/2155
	// datetime,8字节，日期时间 '2008-12-2 22:06:44'，1000-01-01 00:00:00/9999-12-31 23:59:59
	DATETIME("java.util.Date", "datetime", "TIMESTAMP"),
	// timestamp,4字节，1970-01-01 00:00:00/2038开始，结束时间是第 2147483647(2^31-1) 秒，北京时间 2038-1-19 11:14:07
	TIMESTAMP("java.util.Date", "timestamp", "TIMESTAMP");
	
	private String   javaType;
	private String   mySqlType;
	private String   mybatisType;
	
	private EnuFieldType(String javaType, String mySqlType, String mybatisType) {
		this.javaType = javaType;
		this.mySqlType = mySqlType;
		this.mybatisType = mybatisType;
	}

	public String getJavaType() {
		return javaType;
	}

	public String getMySqlType() {
		return mySqlType;
	}

	public String getMybatisType() {
		return mybatisType;
	}
}
