/*!
 * EIC PresentationController
 * Copyright 2012, Multimedia Lab - Ghent University - iMinds
 * Licensed under GPL Version 3 license <http://www.gnu.org/licenses/gpl.html> .
 */
define(['eic-gui-master/client/scripts/lib/jquery', 'eic-gui-master/client/scripts/eic/Logger', 'eic-gui-master/client/scripts/eic/FacebookConnector',
  'eic-gui-master/client/scripts/eic/generators/IntroductionSlideGenerator', 'eic-gui-master/client/scripts/eic/generators/OutroductionSlideGenerator', '../eic/generators/TopicToTopicSlideGenerator',
  'eic-gui-master/client/scripts/eic/generators/TopicToTopicSlideGenerator2', 'eic-gui-master/client/scripts/eic/generators/CompositeSlideGenerator',
  'eic-gui-master/client/scripts/eic/generators/ErrorSlideGenerator', 'eic-gui-master/client/scripts/eic/SlidePresenter', 'eic-gui-master/client/scripts/eic/TopicSelector', 'eic-gui-master/client/scripts/eic/SlideEditor',  'eic-gui-master/client/scripts/config/URLs',],
  function ($, Logger, FacebookConnector,
    IntroductionSlideGenerator, OutroductionSlideGenerator, TopicToTopicSlideGenerator,
    TopicToTopicSlideGenerator2, CompositeSlideGenerator,
    ErrorSlideGenerator, SlidePresenter, TopicSelector, SlideEditor, urls) {
    "use strict";
    var logger = new Logger("PresentationController");

    function PresentationController() {
      this.facebookConnector = new FacebookConnector();
      this.topicSelector = new TopicSelector(this.facebookConnector);
    }

    /* Member functions */

    PresentationController.prototype = {
      init: function () {
        logger.log("Initializing");
        var self = this;
        /*this.facebookConnector.init();

        // Select the topic when the user connects to Facebook
        // and prepare the introduction slide.
        this.facebookConnector.on('connected', function (event, profile) {
          logger.log("Connected to Facebook as", profile.name);
          self.profile = profile;
          self.topicSelector.selectTopic().then(
            function (startTopic) { self.startTopic = startTopic; },
            function (error) { self.startTopic = new Error(error); });
        });
        this.facebookConnector.on('disconnected', function () {
          logger.log("Disconnected from Facebook");
          delete self.profile;
          delete self.startTopic;
        });*/
      },

      // Lets the user connect with a Facebook account.
      connectToFacebook: function () {
        this.facebookConnector.connect();
      },

      // Starts the movie about the connection between the user and the topic.
      playMovie: function () {
        //if (!this.startTopic) throw "No start topic selected.";
        //if (!this.endTopic) throw "No end topic selected.";
        //if (!this.intro) throw "The introduction was not initialized";

        // Create the slides panel
        var $slides = $('<div>').addClass('slides'),
            $wrapper = $('<div>').addClass('slides-wrapper')
                                 .append($slides);

        // Hide the main panel and show the slides panel
        $('#screen').append($wrapper);
        //$wrapper.hide().fadeIn($.proxy($slides.hide(), 'fadeIn', 1000));

        // Add introduction, body, and outroduction generators
        //logger.log("Creating slides from", this.startTopic.label, "to", this.endTopic.label);
        var generator = new CompositeSlideGenerator();
        
        $.ajax({
                type: "GET",
                url: urls.singlepath,
                dataType: "jsonp",
                error: function () {
                  self.addGenerator(new ErrorSlideGenerator('No path between found.'));
                  self.loader.stopWaiting();
                },
                success: function (path) {
					this.startTopic=path.source;
					this.endTopic=path.destination;
					
					generator.addGenerators([
						new IntroductionSlideGenerator(this.startTopic, this.profile),
						new TopicToTopicSlideGenerator2(path),
						new OutroductionSlideGenerator(this.profile || this.startTopic, this.endTopic)
					]);
        
					//To prevent any slide-skipping, don't go into editor mode until all slides are at least done (waiting on topic slide audio)   
					// I know that the second generator in the array is the one with topic slides...    
					if (generator.generators[1].ready){
						new SlidePresenter($slides, generator).start();
					}
					else{
						generator.generators[1].once('topic slides ready', function(){new SlidePresenter($slides, generator).start()});
					}
                }
           });		
      }
    };

    /* Properties */

    // The startTopic property also initializes the introduction,
    // so the movie can be buffered earlier and thus start faster.
    Object.defineProperty(PresentationController.prototype, "startTopic", {
      get: function () { return this._startTopic; },
      set: function (startTopic) {
        logger.log("Start topic set to", startTopic.label);
        this._startTopic = startTopic;
        delete this.intro;

        // If the topic is an error, show the error slide
        if (startTopic instanceof Error)
          this.intro = new ErrorSlideGenerator(startTopic);
        // Otherwise, create an actual introduction slide
        else
          this.intro = new IntroductionSlideGenerator(startTopic, this.profile);
        // Initialize the intro right away, avoiding delay when starting the movie
        this.intro.init();
      }
    });

    return PresentationController;
  });