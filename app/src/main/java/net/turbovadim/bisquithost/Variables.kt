package net.turbovadim.bisquithost

import net.turbovadim.bisquithost.network.ServerAttributes
//
////Serverlist Variables
//var canBeClicked = true

//ServerControl Variables
var ServerId = ""
var Name = ""
var RamLimit: Int = 0
var CpuLimit: Int = 0
var DiskLimit: Int = 0
var SftpUrl: String = ""
var BackupsLimit: Int = 0

//ApiRequest Variables
var TotalServers: Int = 0

val serverAttributes = mutableListOf<ServerAttributes>()

var Username: String = ""