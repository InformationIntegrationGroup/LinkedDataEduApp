define(['eic-gui-master/client/scripts/lib/jvent', 'eic-gui-master/client/scripts/eic/Logger','eic-gui-master/client/scripts/eic/pluginsniff'],function(EventEmitter, Logger) {    
    var plugintype;
    var logger = new Logger("WAVPlayer");
      
    if (Audio){
        if (document.createElement('audio').canPlayType("audio/wav"))
            plugintype="Audio";
        else
            plugintype=Plugin.getPluginsForMimeType("audio/wav");
    }
    else{
        plugintype=Plugin.getPluginsForMimeType("audio/wav");
    }
  
    function WAVPlayer(){
        EventEmitter.call(this);
        this.TrackCount=0;
        this.CurrentTrack=1;
        this.playing=false; 
        this.html_obj;
        this.IntervalCheck;
          
        if (plugintype!="Audio" && plugintype!="QuickTime" && plugintype!="Windows Media" && plugintype!="VLC"){
            alert("Your browser does not support our audio. Please download an appropriate plugin (QuickTime, Windows Media Player, or VLC)");
        }
  
        html_obj=document.createElement("div");
        document.body.appendChild(html_obj);
    };
  
    WAVPlayer.prototype = {
      
    addTrack: function(snd_url, autoplay){  
        
	if (plugintype=="Audio"){
             $(html_obj).append(
                "<audio id='track" + ++this.TrackCount + "' src='"+snd_url+"' controls='false' hidden='true'/>");
        }
        else if (plugintype=="QuickTime"){
             $(html_obj).append(
                "<embed id='track" + ++this.TrackCount + "' src='" + snd_url + "' width='1' height='1' controller='false' enablejavascript='true' autoplay='false' loop='false'>");
        }
        else if (plugintype=="Windows Media"){
             $(html_obj).append(
                "<embed id='track" + ++this.TrackCount + "' src='" + snd_url + "' width='1' height='1' Enabled='false' AutoStart='false' ShowControls='false'>");
        }
        else if (plugintype=="VLC"){
             $(html_obj).append(
                "<embed id='track" + this.TrackCount++ + "' target='" + snd_url + "' width='1' height='1' autoplay='false' controls='false'>");
        }
        logger.log("created audio object track" + this.TrackCount);
        if (autoplay)
            this.playSound(0,false);
    },
      
    playSound: function(attempt,ignore){
		//Prevent multiple calls for playthrough and triggering of the interval function
		if (this.playing && !ignore)
			return;
		
		if (!this.TrackCount){
            logger.log("Something strange happened");
            return;
        }
		
		this.playing=true;
		var self = this;
        var success=false;
        if (!attempt)
            attempt=0;
          
        try{
            if (plugintype=="Audio")
                document.getElementById("track"+ this.CurrentTrack).play();
            else if (plugintype=="QuickTime" || plugintype=="Windows Media")
                document.getElementById("track"+ this.CurrentTrack).Play();
            else if (plugintype=="VLC"){
                try{
                    document.getElementById("track"+ this.CurrentTrack).play;
                }
                catch(e){
                    document.getElementById("track"+ this.CurrentTrack).playlist.play();
                }
            }
            success=true;
        }
        catch(err){
            logger.log("Playback attempt" + ++attempt + " of track" + this.CurrentTrack + " failed");
            if (attempt < 20)
                window.setTimeout(function(){self.playSound(attempt,true)},1000);
            else{
                logger.log("Gave up on playing audio");
                self.emit('playback_error');
            }
        }
        if (success){
            logger.log("Playing track"+this.CurrentTrack);
            this.IntervalCheck=window.setInterval(function(){self.CheckforPlay()},1000);
        }
    },
      
    CheckforPlay: function(){   
        if (!this.isPlaying()){
            window.clearInterval(this.IntervalCheck);
            $(document.getElementById("track"+this.CurrentTrack)).remove();
			this.CurrentTrack++;
            this.nextTrack();
        }
    },
      
    nextTrack: function(){
        if (this.CurrentTrack>this.TrackCount){
			this.playing=false;
			this.emit('wav_ended');
            logger.log("All audio files in queue have been played");
            return;
        }
        this.playSound(0,true);
    },
      
    isPlaying: function(){
        if (this.CurrentTrack>this.TrackCount){
            logger.log("no tracks loaded");
            return false;
        }
          
        if (plugintype=="Audio"){
            if (document.getElementById("track"+ this.CurrentTrack).currentTime == document.getElementById("track"+ this.CurrentTrack).duration){
                return false;
            }
        }
        else if (plugintype=="QuickTime"){
            if (document.getElementById("track"+ this.CurrentTrack).GetTime() == document.getElementById("track"+ this.CurrentTrack).GetEndTime()){ 
                return false;
            }
        }
        else if (plugintype=="Windows Media"){
            if (document.getElementById("track"+ this.CurrentTrack).CurrentPosition == document.getElementById("track"+ this.CurrentTrack).Duration){
                return false;
            }
        }
        else if (plugintype=="VLC"){
            try{
                if (document.getElementById("track"+ this.CurrentTrack).get_position() == document.getElementById("track"+ this.CurrentTrack).get_length()){
                    return false;
                }
            }
            catch(e){
                try{
                    if (document.getElementById("track"+ this.CurrentTrack).input.time == document.getElementById("track"+ this.CurrentTrack).input.length){
                        return false;
                    }
                }
                catch(err){
                    alert("VLC failed");
                    window.clearInterval(IntervalCheck);
                }
            }
        }
        return true;
    }
    };
      
    return WAVPlayer;
});


