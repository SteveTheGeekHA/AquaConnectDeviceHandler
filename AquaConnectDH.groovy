/**
 *  Copyright 2015 SmartThings
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
 
import groovy.json.JsonSlurper

metadata {
	definition (name: "Hayward Aqua Connect", namespace: "SteveTheGeekAH", author: "Steve The Geek") {
        capability "Polling"
        capability "Refresh"
        capability "Switch"
        
        command "filterOn"
        command "filterOff"
        command "lightsOn"
        command "lightsOff"        
        command "aux1On"
        command "aux1Off"
        command "aux2On"
        command "aux2Off"   
        command "changeMode"
        
        attribute "displayLine1", "String"
        attribute "displayLine2", "String"
        attribute "spaTemp", "String"
        attribute "poolTemp", "String"
        attribute "airTemp", "String"
        attribute "lightsStatus", "String"
        attribute "filterStatus", "String"
        attribute "currentMode", "String"
        attribute "heaterStatus", "String"
        attribute "aux1Status", "String"
        attribute "aux2Status", "String"
        
	}

	simulator {

	}
    
	tiles {
    
		valueTile("currentMode", "currentMode", width: 1, height: 1, canChangeIcon: true, decoration: "flat") {
			state "default", label: '\n\n${currentValue}', action: "changeMode", backgroundColor: "#ffffff", icon: "st.Health & Wellness.health2"
		} 

		valueTile("poolTemp", "poolTemp", width: 1, height: 1, decoration: "flat") {
			state "default", label: '${currentValue}', backgroundColor: "#ffffff"
		} 

		valueTile("spaTemp", "spaTemp", width: 1, height: 1, decoration: "flat") {
			state "default", label: '${currentValue}', backgroundColor: "#ffffff"
		} 

		valueTile("airTemp", "airTemp", width: 1, height: 1, decoration: "flat") {
			state "default", label: '${currentValue}', backgroundColor: "#ffffff"
		} 

     	standardTile("lightsStatus", "lightsStatus", width: 1, height: 1, decoration: "flat") {
            state "off", label: 'Lights ${currentValue}', action: "lightsOn",
                  icon: "st.Lighting.light21", backgroundColor: "#ffffff"
            state "on", label: 'Lights ${currentValue}', action: "lightsOff",
                  icon: "st.Lighting.light21", backgroundColor: "#79b821"
        }

		standardTile("filterStatus", "filterStatus", width: 1, height: 1, decoration: "flat") {
            state "off", label: 'Filter ${currentValue}', action: "filterOn",
                  icon: "st.Appliances.appliances17", backgroundColor: "#ffffff"
            state "on", label: 'Filter ${currentValue}', action: "filterOff",
                  icon: "st.Appliances.appliances17", backgroundColor: "#79b821"
        }

     	standardTile("heaterStatus", "heaterStatus", width: 1, height: 1, decoration: "flat") {
            state "off", label: 'Heater ${currentValue}',
                  icon: "st.Weather.weather2", backgroundColor: "#ffffff"
            state "on", label: 'Heater ${currentValue}',
                  icon: "st.Weather.weather2", backgroundColor: "#79b821"
        }

      	standardTile("aux1Status", "aux1Status", width: 1, height: 1, decoration: "flat") {
            state "off", label: 'Aux1 ${currentValue}', action: "aux1On",
                  icon: "st.Appliances.appliances17", backgroundColor: "#ffffff"
            state "on", label: 'Aux1 ${currentValue}', action: "aux1Off",
                  icon: "st.Appliances.appliances17", backgroundColor: "#79b821"
        }

      	standardTile("aux2Status", "aux2Status", width: 1, height: 1, decoration: "flat") {
            state "off", label: 'Aux2 ${currentValue}', action: "aux2On",
                  icon: "st.Appliances.appliances17", backgroundColor: "#ffffff"
            state "on", label: 'Aux2 ${currentValue}', action: "aux2Off",
                  icon: "st.Appliances.appliances17", backgroundColor: "#79b821"
        }

		standardTile("refresh", "device.switch", decoration: "flat", width: 1, height: 1) {
            state "default", action:"refresh", icon:"st.secondary.refresh"
        } 

		valueTile("line1", "displayLine1", width: 3, height: 1, decoration: "flat") {
			state "default", label: '${currentValue}', backgroundColor: "#ffffff"
		} 

		valueTile("line2", "displayLine2", width: 3, height: 1, decoration: "flat") {
			state "default", label: '${currentValue}', backgroundColor: "#ffffff"
		}         
        
		main "currentMode"
		details(["currentMode", "poolTemp", "spaTemp", "airTemp", "filterStatus", "lightsStatus", "heaterStatus", "aux1Status", "aux2Status", "refresh", "line1","line2"])
	}

    preferences {

	}

}

def parse(String description) {
    def message = parseLanMessage(description)
    //log.debug(message)
    
    def responseLines = message.body.substring(message.body.indexOf("<body>")+6, message.body.indexOf("</body>")).tokenize('\n')
	responseLines = responseLines.drop(1)
	responseLines = responseLines.take(3)
    
    
    def line1 = responseLines[0].substring(0, responseLines[0].length()-1)

	//Clean up the string
	line1 = line1.substring(0, line1.length()-3)
    line1 = line1.replaceAll("&#176", "°")
    line1 = line1.replaceAll("[\u0000-\u001f]", "")
    line1 = line1.replaceAll("&nbsp;", " ")
    while(line1.startsWith(" ")) {
       line1 = line1.substring(1)
    }
        
    if(line1.contains("Spa Temp")) {
    	log.debug("Spa temp changed");
    	sendEvent(name: "spaTemp", value: line1, isStateChange: true)
    }

    if(line1.contains("Air Temp")) {
    	log.debug("Air temp changed");
    	sendEvent(name: "airTemp", value: line1, isStateChange: true)
    }

    if(line1.contains("Pool Temp")) {
    	log.debug("Pool temp changed");
    	sendEvent(name: "poolTemp", value: line1, isStateChange: true)
    }

    log.debug(line1)
	sendEvent(name: "displayLine1", value: line1, isStateChange: true)
    
    def line2 = responseLines[1].substring(0, responseLines[1].length()-1)

	//Clean up the string
    line2 = line2.substring(0, line2.length()-3)
    line2 = line2.replaceAll("&#176", "°")
    line2 = line2.replaceAll("[\u0000-\u001f]", "")
    line2 = line2.replaceAll("&nbsp;", " ")
    while(line2.startsWith(" ")) {
       line2 = line2.substring(1)
    }

	log.debug(line2)
	sendEvent(name: "displayLine2", value: line2, isStateChange: true)

	def line3 = responseLines[2].substring(0, responseLines[2].length()-1)
    line3 = line3.substring(0, line3.length()-3)

    log.debug("leds : ${line3}")
    
	if(getFirstLedStatus(line3.charAt(0)) == "on") {
    	sendEvent(name: "currentMode", value: "Pool", isStateChange: true)
    }
	if(getSecondLedStatus(line3.charAt(0)) == "on") {
    	sendEvent(name: "currentMode", value: "Spa", isStateChange: true)
    }
	if(getFirstLedStatus(line3.charAt(1)) == "on") {
    	sendEvent(name: "currentMode", value: "Spillover", isStateChange: true)
    }
    
    sendEvent(name: "filterStatus", value: getSecondLedStatus(line3.charAt(1)), isStateChange: true)
    sendEvent(name: "lightsStatus", value: getFirstLedStatus(line3.charAt(2)), isStateChange: true)
    sendEvent(name: "heaterStatus", value: getFirstLedStatus(line3.charAt(3)), isStateChange: true)
    sendEvent(name: "aux1Status", value: getSecondLedStatus(line3.charAt(4)), isStateChange: true)
    sendEvent(name: "aux2Status", value: getFirstLedStatus(line3.charAt(5)), isStateChange: true)
    
}

def poll() {
    log.debug("polled")
    refresh()
}

def refresh() {
	return createGetRequest("WNewSt.htm");
}

def filterOn() {
	sendEvent(name: "filterStatus", value: "on", isStateChange: true)
	return postKey("WNewSt.htm", "08");
}

def filterOff() {
	sendEvent(name: "filterStatus", value: "off", isStateChange: true)
	return postKey("WNewSt.htm", "08");
}
def lightsOn() {
	sendEvent(name: "lightsStatus", value: "on", isStateChange: true)
	return postKey("WNewSt.htm", "09");
}

def lightsOff() {
	sendEvent(name: "lightsStatus", value: "off", isStateChange: true)
	return postKey("WNewSt.htm", "09");
}

def aux1On() {
	sendEvent(name: "aux1Status", value: "on", isStateChange: true)
	return postKey("WNewSt.htm", "0A");
}

def aux1Off() {
	sendEvent(name: "aux1Status", value: "off", isStateChange: true)
	return postKey("WNewSt.htm", "0A");
}

def aux2On() {
	sendEvent(name: "aux2Status", value: "on", isStateChange: true)
	return postKey("WNewSt.htm", "0B");
}

def aux2Off() {
	sendEvent(name: "aux2Status", value: "off", isStateChange: true)
	return postKey("WNewSt.htm", "0B");
}

def changeMode() {
	sendEvent(name: "aux2Status", value: "off", isStateChange: true)
	return postKey("WNewSt.htm", "07");
}

// gets the address of the hub
private getCallBackAddress() {
    return device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")
}

private createGetRequest(String url) {

	log.debug("/${url}")
    
    def result = new physicalgraph.device.HubAction(
            method: "GET",
            path: "/${url}",
            headers: [
                HOST: getHostAddress()
            ]
        )

        return result;
}

private postKey(String url, String key) {

	log.debug("/${url}")
    log.debug("KeyId:"+key);
    
    def result = new physicalgraph.device.HubAction(
            method: "POST",
            path: "/${url}",
            body: "KeyId="+key,
            headers: [
                HOST: getHostAddress(),
                "Content-Type": "application/x-www-form-urlencoded"
            ]
        )

        return result;
}

// gets the address of the device
private getHostAddress() {
    def ip = getDataValue("ip")
    def port = getDataValue("port")

    if (!ip || !port) {
        def parts = device.deviceNetworkId.split(":")
        if (parts.length == 2) {
            ip = parts[0]
            port = parts[1]
        } else {
            log.warn "Can't figure out ip and port for device: ${device.id}"
        }
    }

    log.debug "Using IP: ${convertHexToIP(ip)} and port: ${convertHexToInt(port)} for device: ${device.id}"
    return convertHexToIP(ip) + ":" + convertHexToInt(port)
}

private Integer convertHexToInt(hex) {
    return Integer.parseInt(hex,16)
}

private String convertHexToIP(hex) {
    return [convertHexToInt(hex[0..1]),convertHexToInt(hex[2..3]),convertHexToInt(hex[4..5]),convertHexToInt(hex[6..7])].join(".")
}

//From this section on : leveraging JS code for AquaConnect HTTP Server, so all this code is copyrighted to the Hayward team
private String getFirstLedStatus(asciiByte) {
	return decodeRawLedData(extractNibbles(asciiByte).charAt(0))
}

private String getSecondLedStatus(asciiByte) {
	return decodeRawLedData(extractNibbles(asciiByte).charAt(1))
}

private String extractNibbles(asciiByte) 
{
	def TwoChars; 

    switch (asciiByte)
    {
    	case "3":
        	TwoChars = "33"; 
        	break;
      	case "4":
        	TwoChars = "34"; 
        	break;
      	case "5":
        	TwoChars = "35"; 
        	break;
      	case "6":
        	TwoChars = "36"; 
        	break;
    	case "C":
        	TwoChars = "43"; 
        	break;
      	case "D":
        	TwoChars = "44"; 
        	break;
      	case "E":
        	TwoChars = "45"; 
        	break;
      	case "F":
        	TwoChars = "46"; 
        	break;

    	case "S":
        	TwoChars = "53"; 
        	break;
      	case "T":
        	TwoChars = "54"; 
        	break;
      	case "U":
        	TwoChars = "55"; 
        	break;
      	case "V":
        	TwoChars = "56"; 
        	break;
    	case "c":
        	TwoChars = "63"; 
        	break;
      	case "d":
        	TwoChars = "64"; 
        	break;
      	case "e":
        	TwoChars = "65"; 
        	break;
      	case "f":
        	TwoChars = "66"; 
        	break;
      default:
        TwoChars = "00"; ; 
    }

	return TwoChars;	

} 

private decodeRawLedData(NibData)
{
    def StrClassData;

    switch ( NibData )
    {
    	case "3":
        	StrClassData = "NOKEY"; 
        	break;
      	case "4":
        	StrClassData = "off"; 
        	break;
      	case "5":
        	StrClassData = "on"; 
        	break;
      	case "6":
        	StrClassData = "BLINK" ; 
        	break;
      default:
        StrClassData = "WEBS_NOKEY"; 
    }
	
    return StrClassData; 

} 
