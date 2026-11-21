// JavaScript Document

		function keyIntegerCheck(eventObj, obj) {
	 		 		 var keyCode;
	 		 
	 		 		 if (document.all){ 
	 		 		 		 keyCode=eventObj.keyCode;
	 		 		 }else{
	 		 		 		 keyCode=eventObj.which;
	 		 		 }
	 		 		 if(keyCode==13){ 
	 		 		 		 return false;
	 		 		 }
	 		 		 
	 		 		 if((keyCode<48 || keyCode >58) && (keyCode != 8 ) && (keyCode!=0)){
	 		 		 		 return false;
	 		 		 }
	 		 
	 		 		 return true;
	 		 }
		
		
		function SoloLetras(event){ 
			var browser = navigator.appName;
    		var Key = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;
				  if (browser=="Microsoft Internet Explorer") {
		        	 var tecla = ((document.all) ? window.event.keyCode: window.event.which ); 
						if(tecla < 45 || tecla > 57){
							event.returnValue = true;
						}else{
							 return false;
						}
				}else{
				     if( Key < 45 || Key >57) return true;
				     else return false;
				 }
				  
				  return true;
			}
		
		function keyPulsadaCheck(eventObj, obj) {
				var correctos = new Array("1","2","3","4","5","6","7","8","9","0"," ","-");
			      	var estado=false;
			     	var codigoTecla=""; 
			     	var cadenaTecla="";
			     	var i;
			        estado=false;
			          if(document.all){
			              codigoTecla = event.keyCode;
			              cadenaTecla = (String.fromCharCode(event.keyCode));
			          }else if(document.layers){
			              codigoTecla = e.which;
			              cadenaTecla = String.fromCharCode(e.which);
			          }else if(document.getElementById){
			              codigoTecla = (window.Event) ? e.which : e.keyCode;
			              cadenaTecla=(String.fromCharCode(codigoTecla));
			          }
			          for(i=0;i<correctos.length;i++){
			              if(cadenaTecla==correctos[i]) 
			                  estado=true;
			          }
			          if(estado==false){
			              if(document.all)
			                  event.returnValue = false;
			              else
			                  return false;
			          }

			          return true;
			}
		
		
		function keyPulsadaCheckNegativos(eventObj, obj,partida) {
					var correctos = new Array("1","2","3","4","5","6","7","8","9","0","-");
			      	var estado=false;
			     	var codigoTecla="";
			     	var cadenaTecla="";   
			     	var i;
			          if(document.all){
			              codigoTecla = event.keyCode;
			              cadenaTecla = (String.fromCharCode(event.keyCode));
			          }else if(document.layers){
			              codigoTecla = e.which;
			              cadenaTecla = String.fromCharCode(e.which);
			          }else if(document.getElementById){
			              codigoTecla = (window.Event) ? e.which : e.keyCode;
			              cadenaTecla=(String.fromCharCode(codigoTecla));
			          }
			          for(i=0;i<correctos.length;i++){
			              if(cadenaTecla==correctos[i])
			                  estado=true;
			          }
			          if(estado==false){
			              if(document.all)
			                  event.returnValue = false;
			              else
			                  return false;
			          }
	
			          return true;
		}