/*!
 * EIC TTSService
 * Copyright 2012, Multimedia Lab - Ghent University - iMinds
 * Licensed under GPL Version 3 license <http://www.gnu.org/licenses/gpl.html> .
 */
define(['http://code.jquery.com/jquery-1.9.1', 'eic-gui-master/client/scripts/eic/Logger', 'eic-gui-master/client/scripts/lib/jvent', 'eic-gui-master/client/scripts/config/URLs', 'eic-gui-master/client/scripts/lib/base64_handler'],
  function ($, Logger, EventEmitter, urls) {
    "use strict";
    var logger = new Logger("TTSService");
    
    //IE is the only current browser without data: uri support, so check if it supports Blobs...otherwise we must wait for synthesis during the embedding phase...
    var urlType;
    if (navigator.userAgent.indexOf('MSIE') !=-1){
		if (window.URL.createObjectURL)
			urlType=1; //Since comparing strings is hard, let's use integers: 	1=objectURL 2=normal URL 3=data:uri
		else
			urlType=2;
	}
	else if (window.URL.createObjectURL)	//Try to get the objectURL method...otherwise fall back to data:uri
		urlType=1;
	else
		urlType=3;
			

    function TTSService() {
      EventEmitter.call(this);
    }

    TTSService.prototype = {
      getSpeech: function (text, lang, callback) {
        var self = this;
        var speech_url;
		speech_url = urls.festivalcheck;

        logger.log('Requesting audio URL ' + text);
        sendSpeech(0);
        
		function sendSpeech(attempt){
	        $.ajax({
	          url: speech_url,
	          type: 'GET',
	          data: { req_text: text, url_type: urlType},
	          dataType: 'jsonp',
	          success: function (data) {
				if (urlType==3)				//data:uri method
					data.snd_url+=data.text;
				else if (urlType == 1){		//createObjectURL method...only available for new browsers
					var blob = new Blob([base64DecToArr(data.text)], {type: "audio/wav"});
	      			data.snd_url = window.URL.createObjectURL(blob);
				}
				else 			//Slow url method...just for IE 9 and under
					data.snd_url+=window.escape(data.text);
					
	            if (data.res === 'OK') {
	              if (callback)
	                callback(data);
	              logger.log('Received audio URL', text + 'url:' + data.snd_url);
	              self.emit('speechReady', data);
	            }
	            else {
	              if (attempt==4){
					logger.log('Error receiving speech', data);
					alert("Speech synthesis error");
					self.emit('speechError', data);
	              }
	              else{
					  logger.log('Error with speech, new attempt: ' + attempt);
					  sendSpeech(attempt+1);
				  }
	            }
	          },
	          error: function (error) {
	            if (attempt==4){
					logger.log('Error receiving speech', error);
					alert("Speech synthesis error");
					self.emit('speechError', error);
	              }
	              else{
					  logger.log('Error with speech, new attempt: ' + attempt);
					  sendSpeech(attempt+1);
				  }
	          }
	        });
		}
      }
    };

    return TTSService;
  });