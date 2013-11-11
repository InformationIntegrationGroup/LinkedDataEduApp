/**
 * @author Dipa
 */
define(['lib/jquery', 'eic/Logger', 'eic/TTSService',
  'eic/generators/CompositeSlideGenerator', 'eic/generators/GoogleImageSlideGenerator',
  'eic/generators/GoogleMapsSlideGenerator', 'eic/generators/TitleSlideGenerator', 'eic/generators/YouTubeSlideGenerator'],
  function ($, Logger, TTSService,
    CompositeSlideGenerator, GoogleImageSlideGenerator,
    GoogleMapsSlideGenerator, TitleSlideGenerator, YouTubeSlideGenerator) {
    "use strict";
    var logger = new Logger("CustomSlideGenerator");

    /*
    * CLEANUP
    **/

    function CustomSlideGenerator(topic, description) {
      CompositeSlideGenerator.call(this);

      this.generatorsHash = {}; //take care of this
      this.generators = [];
      this.topic = topic;
      this.description = description;
      this.durationLeft = 0;
      this.audioURL = '';
      
      //stuff
      this.curSlide = null;
      this.slides = {};
    }

    $.extend(CustomSlideGenerator.prototype,
             CompositeSlideGenerator.prototype,
      {
        /** Checks whether at least one child generator has a next slide. */
        hasNext: function () {
          if(this.curSlide != null) return true;
          else return false;
        },

        /** Initialize all child generators. */
        init: function () {
          if (this.inited)
            return;

          //Create all generators depending on the type of the topic
          switch (this.topic.type) {
          case "http://dbpedia.org/ontology/PopulatedPlace":
            this.addCutomGenerator(new GoogleImageSlideGenerator(this.topic), false, "img");
            this.addCustomGenerator(new YouTubeSlideGenerator(this.topic), false, "vid");
            this.addCustomGenerator(new GoogleMapsSlideGenerator(this.topic), false, "img");
            break;
          default:
            this.addCustomGenerator(new GoogleImageSlideGenerator(this.topic), false, "img");
            this.addCustomGenerator(new YouTubeSlideGenerator(this.topic), false, "vid");
            break;
          }

          var tts = new TTSService(),
              self = this;
          tts.once('speechReady', function (event, data) {
            self.durationLeft = Math.floor(data.snd_time);
            self.audioURL = data.snd_url;
            logger.log('Received speech for topic', self.topic.label);
            // When speech is received, 'remind' the presenter that the slides are ready
            self.emit('newSlides');
          });
          logger.log('Getting speech for topic', this.topic.label);
          tts.getSpeech(this.description, 'en_GB');

          this.inited = true;
        },
        
        addCustomGenerator: function (generator, suppressInit, type) {        	
        	// initialize the generator and add it to the list
      		if (!suppressInit) generator.init();
      		this.generators.push(generator);
      		switch (type) {
      			case "img":
      				this.generatorsHash["img"] = generator;
      			case "vid":
      				this.generatorsHash["vid"] = generator;
      		}
      		// signal the arrival of new slides
      		generator.on('newSlides', this.emitNewSlidesEvent);
      		if (generator.hasNext())
        		this.emitNewSlidesEvent();
        },
        
        prepare: function () {
          this.curSlide = new TitleSlideGenerator(this.topic).next();
          this.curSlide.audioURL = this.audioURL;

          // prepare other generators
          this.generators.forEach(function (g) { g.prepare(); });

          //add all the slides for each generator
          for(var val in this.generatorsHash){
          	var s = [];
          	for(var i = 0; i < 3 && this.generatorsHash[val].hasNext() && 
          		this.generatorsHash[val].next !== undefined; i++){
          		s.push(this.generatorsHash[val].next());
          	}
          	this.slides[val] = s;
          }
          	
          logger.log('Added slides on ', this.topic.label);
        },
        
        next: function () {
        	return this.curSlide;
        },
        
        
        getSlides: function() {
        	return this.slides;
        },
        
        setCurSlide: function (slide) {
        	this.curSlide = slide;	
        },
        
        getTest: function () {
        	return this.testSlides;
        }
      });
    return CustomSlideGenerator;
  });


