(function (requirejs) {
  "use strict";

  requirejs.config({
    shim: {
      'lib/jquery': {
        exports: 'jQuery'
      },
      'lib/jquery.ui.core': {
        deps: ['lib/jquery']
      },
      'lib/jquery.ui.widget': {
        deps: ['lib/jquery.ui.core']
      },
      'lib/jquery.ui.position': {
        deps: ['lib/jquery.ui.core']
      },
      'lib/jquery.ui.autocomplete': {
        deps: ['lib/jquery.ui.core', 'lib/jquery.ui.widget', 'lib/jquery.ui.position']
      },
      'lib/jvent': {
        exports: 'jvent'
      },
      'lib/jplayer.min': {
        deps: ['lib/jquery']
      },
      'lib/prefixfree.jquery': {
        deps: ['lib/prefixfree.min']
      }
    },
  });

  require(['eic/PresentationController', 'eic/PiecesUI', 'eic/SlideEditor'], function (PresentationController, PiecesUI, SlideEditor) {
    var controller = new PresentationController();
    var editor = new SlideEditor();
    var view = new PiecesUI(controller, editor);
    controller.init();
    view.init();
    editor.init();
  });
})(requirejs);

