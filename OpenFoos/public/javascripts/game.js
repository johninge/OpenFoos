// Defines application namespace
    var Foosball = Foosball || {};

$(document).ready(function()
{    
    // Defines the API base-url used by models 
    // upon GET, POST, PUT and DELETE requests
    window.serverURL = "/api";
    
    // Extends the Backbone View with a helper function 
    // that is activated when we destroy views. 
    // This method triggers a close call, removes itself from the 
    // HTML document and unbinds itelf from other models.
    
    Backbone.View.prototype.close = function()
    {
        var self = this;
        self.trigger('close');
        self.remove();
        self.unbind();
        
        // If an onClose function exists on the view, 
        // then it will be called before end 
        if (self.onClose)
        {
            self.onClose();
        }       
    }
    
    /************************************************************************/
    /*  MODELS
    /************************************************************************/
    
    Foosball.PlayerModel = Backbone.Model.extend
    ({
        url: window.serverURL + '/player/login',
        
        // Authenticates and saves the player to the server.
        // Options parameter indicates what callback functions should be run 
        // if the communication with the server is ended and successful
        authenticate: function(options)
        {
            var url = this.url;
            if(options.url)
                url = options.url; 
            
           var settings = 
               {
                    url: url,
                    async: true,
                    success: options.success,
                    error: options.error,
                    wait:true
               }
               
               this.save({}, settings);
        },

        // Indicates whether the player is authenticated or not, 
        // based on if the player id is set.
        isAuthenticated: function()
        {
          return this.get('id') !== undefined;  
        },
        
        // Resets the player attributes to 
        // default values
        reset: function()
        {
            this.set(this.defaults);
        },
        
        // Indicates whether this player is equal to the player
        // set in the parameter
        equals: function(player)
        {
           return this.get('id') === player.get('id')
                  && this === player; 
        },
        
        // Parses the information responded back from the server after a 
        // successful save. We can choose what values we want to incoperate between the 
        // server version and our client version of the player instance
        parse: function(response) 
        {
            var attrs = {};
            attrs.id = response.id; 
            attrs.username = response.username; 
            attrs.image = response.image; 
            attrs.rfid = response.rfid;
            return attrs;
        },
        
        defaults: 
        {
            'id': undefined,
            'username' : undefined,
            'image': undefined,
            'password': undefined,
            'rfid': undefined
        }
    }); 
    
    Foosball.TeamModel = Backbone.Model.extend({
        url: window.serverURL + '/team',
        
        initialize: function()
        {
            // Initializes both team-players by instantiating 
            // new players
            this.set('player1', new Foosball.PlayerModel());
            this.set('player2', new Foosball.PlayerModel());
        },
        
        // Indicates whether this team contains a given player or not
        contains: function(player)
        {
            return this.get('player1').equals(player) || 
                   this.get('player2').equals(player);
        },
        
        // Indicates if this team contains a player based on specific arguments 
        // given in the comparator. Returns the player if the arguments match
        find: function(comparator)
        {
            var players = [this.get('player1'), this.get('player2')]; 
            var results = _.find(players, comparator); 
            return results;
        },

        // Return the number of autenticated players in this team
        count: function()
        {
          var count = 0;
          if(this.get('player1').isAuthenticated())   
              count++; 
          if(this.get('player2').isAuthenticated())  
              count++
          
          return count;
        },
        
        // Authenticates and saves the team to the server.
        // Options parameter indicates what callback functions should be run 
        // if the communication with the server is ended and successful or faulty 
        authenticate: function(options)
        {            
            var settings = 
               {
                    async: true,
                    success: options.success,
                    error: options.error,
                    wait:true
               }
               
               this.save({}, settings);
        },
        
        // Indicates whether the team is authenticated or not, 
        // based on if the team id is set.
        isAuthenticated: function()
        {
            return this.get('id') !== undefined; 
        },
        
        // Parses the information responded back from the server. 
        parse: function(response) 
        {
            var attrs = {};
           
            attrs.id = response.id;
            attrs.rating = response.rating;
            attrs.team_name = response.team_name;            
            attrs.won = response.won;            
            attrs.lost = response.lost;            
            
            return attrs;
        },
        
        // Resets each player on the team and sets attributes
        // to default values
        reset: function()
        {
            this.get('player1').reset();
            this.get('player2').reset();
            this.set(this.defaults);
        },
        
        defaults: 
        {
            'id': undefined,
            'team_name': undefined,
            'rating': 0,
            'won': 0,
            'lost': 0
        }
    });

   Foosball.GoalModel = Backbone.Model.extend
   ({
        defaults:
        {
            'player': undefined,    // Indicates the player entity that scored
            'position': undefined, // Indicates the player position on the team
            'game_id': undefined, // Indicates the game that this goal belongs to
            'backfire': false,   // Idicates whether this was an self/own-goal
            'timestamp': 0
        }
   }); 
   
   Foosball.ClockModel = Backbone.Model.extend
   ({       
       
       tick: function()
       {
           // Increments the current time by 1 second.
           this.set('seconds', (this.get('seconds') + 1));
       },
       
       reset: function()
       {
           this.set('seconds', 0);
       },
       
       defaults:
       {
            'seconds': 0
       }
   }); 
   
   Foosball.GoalCollection = Backbone.Collection.extend
   ({
        url: window.serverURL + '/game/goals',
        model: Foosball.GoalModel,   
   
        // Rejects and returns the last goal scored
        pop: function(player) 
        {
            var goal; 
            // Checks if this collection has any goals
            if(this.length === 0)
            {
                return undefined;
            }
            
            // If no player is specified, get the last goal scored
            if(!player)
            {
                goal = this.at(this.length - 1);             
            }

            else
            { 
                // Iterates through all the goals and returns 
                // all goals that are scored by the player given in the parameter 
                var collection = this.filter(function(goal)
                {
                    return goal.get('player').equals(player);
                }); 
                
                // Gets the last goal scored from the collection of 
                // scored goals by that player
                goal = _.last(collection);
            }
            
            if(goal !== undefined)
                this.remove(goal); // Removes the goal from the collection	
            
            return goal;
        },
        
        // Saves the goal collection as a JSON array representation
        save: function()
        {
            var collection = JSON.stringify(this.toJSON());
            $.ajax
            ({
                type: 'POST',
                url: this.url,
                data: collection,
                error: function(){Foosball.Message.showError("An error occured while we attempted to save the goals");},
                dataType: "json"
            });
        }
    });
     
   Foosball.GameModel = Backbone.Model.extend
   ({
        url: window.serverURL + '/game',
        
        initialize: function()
        {
            _.bindAll(this, 'isReady', 'start', 'finish', 'restart', 'reset');
            this.set('home_team', new Foosball.TeamModel({side: 'home'}));
            this.set('visitor_team',new Foosball.TeamModel({side: 'visitor'}));
        },
       
        // Indicates wheter the game has met all minimum requirements to start 
        isReady: function()
        {
            var home = this.get('home_team'),
                visitor = this.get('visitor_team');
            
            // Checks that both teams at least have one player
            if(home.count() === 0 || visitor.count() === 0)
            {
                Foosball.Message.showError("Both teams need at least one player!");    
                return false;
            }
            
            // Both teams need to have an equal amount of players
            // if the game mode is ranked
            if(this.get('mode') === 'ranked')
            {    
                if(home.count() !== visitor.count())
                {
                    Foosball.Message.showError("A ranked game need an equal amount of players on both teams!");    
                    return false;
                }
            }
           
           // Sets the game state 
           if(this.get('state') !== 'started')
           {                            
              this.set('state', 'ready');
           }   
           
           return true;
        },
       
        // Starts the game if all authentication is successfull
        start: function(restart) 
        {
           if(!this.isReady()) 
               return false; 
           
           var self = this;
           
           console.log('is in start and restat is ' + (restart === true ? 'isset' : '!set'));
           
           // Anonymus callback that sets the game state
           // if the authentication of the game is successful
           var startState = function()
           {
              self.set('state', 'started');
           };
           
           // Anonymus callback that begins to authenticate the game
           // if the authentication of the visitor team is successful
           var authenticateGame = function()
           {
               var options = {success: startState}; 
               self.authenticate(options);
           }
           
           // Anonymus callback that begins to authenticate the game
           // if the authentication of the visitor team is successful
           var authenticateVisitor = function()
           {
              var options = {success: authenticateGame};
              self.get('visitor_team').authenticate(options);
           }
           
           // Authenticates the home team and proceeds to 
           // authenticate the visitor team if successful
           var options = {success: authenticateVisitor};
           
           if(restart === true)
           authenticateGame(); 
       
           else
           self.get('home_team').authenticate(options);
           
        },
        
        // Sets the game state to ended. 
        // Trigged when the game is over, but still not finnished or restarted. 
        // State indicated that the game can be resumed if the referee wishes so. 
        end: function()
        {
            this.set('state', 'end');
        },
        
        // Saves the current game and resets
        // it to default values
        finish: function() 
        {
            var self = this;
            var options = 
            { 
                async: true,
                success: function()
                {
                    self.set('state', 'finish'); 
                    self.reset();
                },
                error: self.error,
                wait: true
            };
            
            self.save({}, options); 
        },
       
        // Re-sets some of the game variables to default-values. 
        // We still keep information about authenticated teams and players
        restart: function()
        {    
            var self = this;
            self.set('state', 'restart');
            
            var options = 
            {
                async: true,
                success: function()
                {
                    self.set
                    ({
                        id: undefined, 
                        home_score: 0,
                        visitor_score: 0,
                        state: 'ready'
                    });
                    
                    var restart = true; 
                    self.start(true);
                },
                error: self.error, 
                wait: true
            }; 

            self.save({}, options);
        },
       
        reset: function()
        {
            // Remembers the current game-mode
            var mode = this.get('mode');
            // Re-sets all values to defaults
            this.set(this.defaults);
            // Sets the game mode to what was previously set
            this.set('mode', mode); 
            // Re-sets the teams and players
            this.get('home_team').reset(); 
            this.get('visitor_team').reset(); 
        },
       
        // Indicates that a game is on overtime. 
        // This state is present when a game is ended and the resumed
        // by the referre if the last goal scored is rejected. 
        onOvertime: function()
        {
            this.set('state', 'overtime');
        },
        
        // Authenticates and saves the game to the server.
        // Options parameter indicates what callback functions should be run 
        // if the communication with the server is ended and successful
        authenticate: function(options)
        {
           var self = this;
           this.save({}, {
               async: true,
               success: options.success,
               error: self.error,
               wait:true
           });
        },
        
        error: function()
               {
                 Foosball.Message.showError("An error occured when trying to authenticate the game", false);  
               },
        
         // Indicates whether the game is authenticated or not, 
        isAuthenticated: function()
        {
            var id = this.get('id');
            return id !== undefined && id > 0; 
        },
        
        // Parses the information retrieved by the server. 
        // We are only really interested in if the server has persisted the game and returned 
        // to us the game identification
        parse: function(response)
        {
            var attrs = {};
            attrs.id = response.id;
            return attrs;
        },
        
        defaults:
        {
            'id': undefined,
            'home_score': 0,
            'visitor_score' : 0,
            'start_time': undefined,
            'end_time': undefined,
            'state': 'new',
            'mode' : 'ranked'
        }
    });
    
    
    Foosball.RefereeModel = Backbone.Model.extend
    ({
        initialize: function()
        { 
            this.set('goals', new Foosball.GoalCollection());
            Foosball.Game.on('change:state', this.handleGameState, this);
           
           // Binds keyboard keypresses invoked by the user
           _.bindAll(this, 'scoreByKeypress');
           $(document).bind('keypress', this.scoreByKeypress);
        },
        
        // Helper function used to score a goal 
        // by pressing the numeric keys 1-4 on the keyboard. 
        // Player positions are related as follow: 
        
        // [Home team - player 1] - Key: 1
        // [Home team - player 2] - Key: 2
        // [Visitor team - player 1] - Key: 3
        // [Visitor team - player 2] - Key: 4
        scoreByKeypress: function(e)
        {    
          var state =  Foosball.Game.get('state');  
          if(state === 'started' || state === 'overtime')
          {
              // Gets the numeric value of the key pressed
              // Values for keys 1-4 are coded by values 49-52
              var key = e.keyCode; 
              var player = undefined;
              
              if(key === 49)
              {
                 player = Foosball.Game.get('home_team').get('player1'); 
              }
              else if(key === 50)
              {
                 player = Foosball.Game.get('home_team').get('player2'); 
              }
              else if(key === 51)
              {
                 player = Foosball.Game.get('visitor_team').get('player1'); 
              }
              else if(key === 52)
              {
                 player = Foosball.Game.get('visitor_team').get('player2'); 
              }
              
              if(player !== undefined && player.isAuthenticated())
              {
                  this.addGoal(player, false);
              }
          }
        },

        // Helper function to handle actions upon change of game state
        handleGameState: function()
        {
            var state = Foosball.Game.get('state');
            if(state === 'started')
            {
                this.reset();
                this.startClock();
            }
            else if(state === 'end' || state === 'new')
            {
                this.stopClock();
            }
            else if(state === 'finish' || state === 'restart')
            {
                this.get('goals').save();
            }
            else if(state === 'overtime')
            {
                this.startClock();
            }
        },
        
        // Adds a new goal to the collection when a 
        // player scores. The backfire parameter indicates whether this
        // was a own-goal or not. 
        addGoal: function(player, backfire)
        {
            var goal = new Foosball.GoalModel({
                                           player: player, // saves a refrence to the player that scored
                                           position: player.get('position'), // gets the player team position
                                           game_id:  Foosball.Game.get('id'), // saves the current game id
                                           backfire: backfire, // indicates wether this was an own-goal or not
                                           timestamp: new Date().getTime() // saves the current time in milliseconds
                                         });
            // Adds and saves the goal
            this.get('goals').add(goal);
            
            // Sets the team label basen on whether the player is present on the home or visitor team
            var team =  Foosball.Game.get('home_team').contains(player) ? 'home' : 'visitor';
            
            // Reverses the team label is this was an own-goal
            if(backfire)
            {
                team = team === 'home' ? 'visitor' : 'home';
            }
            
            // Increments the game score by a point
            Foosball.Game.set(team + '_score',  Foosball.Game.get(team + '_score') + 1);
            
            // Displays a message to the users
            Foosball.Message.showMessage(player.get('username') + ' Scored for the ' + team + "team", false);
            
            // Cheks if the game is won and ended. 
            this.isGameWon();
        },
       
        
        removeGoal: function(player)
        {
            // Remove the last goal
            var goal = this.get('goals').pop(player);

            // A goal entity could be undefined if no goals are scored
            if(goal !== undefined)
            {
                // Gets the player that scored the rejected goal and 
                // controls what team this player is located on
                var player = goal.get('player');
                var team =  Foosball.Game.get('home_team').contains(player) ? 'home' : 'visitor';
           
                // Reverses the team label is this was an own-goal
                if(goal.get('backfire'))
                {
                    team = team === 'home' ? 'visitor' : 'home';
                }
                
                // Decrements the game score by a point
                 Foosball.Game.set(team + '_score',  Foosball.Game.get(team + '_score') - 1);
            }
            
            // If the game was ended and a winner was set, but the 
            // players choose to reject the last goal for some reason, then 
            // the game is re-started and set to overtime.
            if( Foosball.Game.get('state') === 'end')
            {
                 Foosball.Game.onOvertime();
            }
        },
        
        // Starts the game clock/timer 
        // It simply checks if an exisiting clock is not set and 
        // starts an interval that increments the time by 1 second, each second.
        startClock: function()
        {
            if(!this.clockInterval)
            {
                this.clockInterval = setInterval(function()
                {
                    Foosball.Clock.tick();
                }, 1000); 
            }
        },
        
        // Clears the current interval and stops the clock
        // Note that the time is not reset and can still be resumed.
        stopClock: function()
        {
           clearInterval(this.clockInterval);
           this.clockInterval = undefined;
        },
        
       
        // Checks if the game should be ended based on rules specified
        isGameWon: function ()
        {
            // Ends the game if one of the teams has scored the 10th goal
            if( Foosball.Game.get('home_score') === 10 ||  Foosball.Game.get('visitor_score') === 10 )
            {
                 Foosball.Game.end();
            }
        },
        
        // Returns the team that has the most goals scored
        // Returns undefined if the score is equal 
        getMatchLeader: function()
        {
           var home_score = Foosball.Game.get('home_score');
           var visitor_score = Foosball.Game.get('visitor_score');
           if(home_score === visitor_score)
           {
               return undefined;
           }
           else if(home_score > visitor_score)
           {
               return Foosball.Game.get('home_team');
           }
           else
           {
               return Foosball.Game.get('visitor_team');
           }
        },
        
        reset:function()
        {
           this.get('goals').reset();
           Foosball.Clock.reset();
        }
    });
    
    /************************************************************************/
    /*  VIEWS
    /************************************************************************/
    
    /**
     * Presents a controller for a player model
     **/
    Foosball.PlayerView = Backbone.View.extend
    ({
        className: 'player-container',
        template: _.template($('#player-template').html()),
        
        events: 
        {
          'click .player-login' : 'login',
          'click .player-logout' : 'logout',
          'click .player-score .player-image' : 'displayOptions', 
          'click .goal-options-cancel' : 'hideOptions',
          'click .goal-options-score' : 'score',
          'click .goal-options-backfire' : 'backfire',
          'click .goal-options-unscore' : 'unscore',
          'click .player-image' : 'focus' 
        },
      
        initialize: function()
        {
            _.bindAll(this, 'render', 'login', 'logout', 'score', 'unscore', 'handleGamestate', 'displayOptions', 'hideOptions', 'backfire', 'focus', 'unfocus');
            this.player = this.options.player;
            this.player.set('position', this.options.position);
            
            this.player.on('change:id', this.render);
            Foosball.Game.on('change:state', this.handleGamestate);
        },
        
        // Changes the background color of the player
        // stance if the plattform is touched/clicked
        focus: function(){
            if(!this.isFocused)
            {
                this.$el.find('.player').css('background-color', '#cfff84');
                this.isFocused = true;
            }
        },

        // Re-sets the background color of the player stance to its default
        // if the player looses focus
        unfocus: function(){
            this.$el.find('.player').css('background-color', 'whiteSmoke');
            this.isFocused = false;
        },
      
        // Renders the player template to an element specified by
        // its parent view (team)
        render: function()
        {
            this.$el.html(this.template(this.player.toJSON()));   
            this.isFocused = false;
            return this;
        },
        
        // Displays login options in the Board. Sends over it's
        // model of the player so that the view is authenticating the same entity
        login: function()
        { 
          Foosball.Board.showLoginView(this.player);  
        },
        
        // Resets the player model to default values. Forgetting 
        // about stored values.
        logout: function()
        {
          this.player.reset();  
        },
        
        // Displays the options for scoring a goal. 
        displayOptions: function()
        {
            this.$el.find('div.goal-options').fadeIn(250);
        },

        // Hides the options for scoring a goal
        hideOptions: function()
        {
            this.$el.find('div.goal-options').fadeOut(250);
            this.unfocus();
        },
        
        // Performs a regular goal 
        score: function()
        {
           Foosball.Referee.addGoal(this.player, false);
           this.hideOptions();
        },
        
        // Rejects the last goal scored by this player
        unscore: function()
        {
           Foosball.Referee.removeGoal(this.player);
          this.hideOptions();
        },
        
        // Performs a own-goal
        backfire: function()
        {
             Foosball.Referee.addGoal(this.player, true);
            this.hideOptions();
        },
        
        // Handles the state of this view based on the game state
        handleGamestate: function()
        {
            if(Foosball.Game.get('state') === 'started')
            {
                if(!this.player.isAuthenticated())
                {
                    // Removes this view completely if the game is started and 
                    // this player model is not authenticated. The user should not be able to 
                    // interact with this element if the game is active
                    this.close(); 
                }
                else
                {
                    // Re-renders itself. Since the game is started and the 
                    // player is authenticated and active, the view should be set 
                    // into a mode where, when clicked, should show options for scoring a goal
                    this.render();
                }
            }
        },
        
        onClose: function()
        {
            // Unbinds the events related to its player model and game
            this.player.off('change:id', this.render);
            Foosball.Game.off('change:state', this.handleGamestate);
        }
    }); 
    
    /**
     * Represents a controller for the team name
     **/
    Foosball.TeamNameView= Backbone.View.extend
    ({
        className: 'team-name-container cursive dark-green',
        initialize: function()
        {   
            this.team = this.options.team;
            // Sets the default team name
            this.defaultName = 'Home team';
            if(this.team ===  Foosball.Game.get('visitor_team'))
                this.defaultName = 'Away team';
            
            this.team.on("change:team_name", this.render, this);
        },
      
        render: function()
        {
            var name =  this.team.get('team_name'); 
            if(!name)
                name = this.defaultName;
            
            this.$el.html('<h2 class="team-name">' + name + "</h2>"); 
            return this;
        }
    });


    /**
     * Represents a controller for the team model
     **/
    Foosball.TeamView= Backbone.View.extend
    ({
        initialize: function()
        {   
            this.team = this.options.team;
            this.render();
        },
      
        render: function()
        {
            // Renders out both players
            var player1View = new Foosball.PlayerView({
                position: 1, 
                player: this.team.get('player1')
            });
            
            var player2View = new Foosball.PlayerView({
                position: 2, 
                player: this.team.get('player2')
            });
            
            // Renders out the team name
            var teamNameView = new Foosball.TeamNameView({team: this.team});
            
            // Appends the objects to it's field area
            this.$el.empty();
            this.$el.append(teamNameView.render().el);
            this.$el.append(player1View.render().el);
            this.$el.append(player2View.render().el);
            return this;
        }
    });
 
 
   /**
    * Controller for player autentication by username and 
    * password or RFID cards. 
    **/ 
    Foosball.LoginView = Backbone.View.extend
    ({
        template: _.template($('#login-template').html()),
        events: 
        {
            'keypress #rfid-input' : 'rfidInput',
            'click #rfid' : 'rfidOption',
            'click #login' : 'authenticateOptions',
            'click #register' : 'register'
        },
      
        initialize: function()
        {
            _.bindAll(this, 'authenticate', 'register', 'isPlayerPresent', 'success', 'error', 'rfidOption', 'rfidInput', 'authenticateOptions');
            this.player = this.options.player; 
        },
        
        // Sets the state for scanning RFID Cards 
        rfidOption: function()
        {
            Foosball.Message.showMessage("Please scan your RFID-card", true); 
            
            var input = this.$el.find('#rfid-input');
            input.val(''); // Resets current card numbers if any exists
            input.focus(); // Focuses on the input field so that all input is entered in it
        },
        
        // Handles key-input of the RFID card number
        rfidInput: function(e)
        {
            // Listnes for the 'Enter'-key to perform the submit to the server
            if (e.keyCode == 13)             
            {
                  // Hides the message rendered by rfidOption()
                  Foosball.Message.hide(); 
                  
                  // Stores the values from the input field
                  var rfid = this.$el.find('#rfid-input').val();
                  
                  // Checks if a player with this RFID number is allready logged in
                  if(this.isPlayerPresent(rfid) === false)
                  {
                      // Stores this number for futher refrence if this id is
                      // new. We want to re-use this if the player wants to be connected to this card.
                      this.lastRFID = rfid;
                      
                      var options = 
                      {
                        url: window.serverURL + '/player/login/rfid/' + rfid,
                        async: true,
                        success: this.success,
                        error: this.error,
                        wait:true
                      };

                   
                    // Performs an GET request to the server
                    // based on the options provided. 
                    //
                    // If the user is found, and properly authenticated, 
                    // Then the server will send us a new instance of the user.     
                   this.player.fetch(options);
                  }
               e.preventDefault();
            }
        },
        
        // Renders the fields and buttons used by the player 
        // for authentication
        render: function()
        {
            this.$el.html(this.template);
            
            // Finds the username input field and connects it to the typeahead function 
            var username = this.$el.find('#username');
            username.typeahead
            ({
                items: 3, // amount of results to be displayed in the list 
                source: function (typeahead, query) {
                    return $.get('/api/player/autocomplete/' + query, // url to the backend service that performs the lookup
                    {
                        query: ''
                    }, 
                    // Prossesing data that is returned by the server
                    function (data) 
                    {
                        return typeahead.process(data);
                    });
                }
            });
            
            // The field is not visible at the instant that this render method is called. 
            // This is because the container that displays the field, is faded in. Some browsers are not able to place focus on 
            // an element that is not 100% visible. 
            setTimeout(function(){
                username.focus();
            },500);

          return this;
        },
  
        // Helper function to indicate if a player is allready authenticated with 
        // a given id
        isPlayerPresent: function(id)
        {
          
          // Used to check if a player with a given username or rfid is 
          // present in one if the teams.
          var comparator = function(player)
          {
              return player.isAuthenticated() && (player.get('username') === id || player.get('rfid') == id);
          }; 
          
          // Performs the search with the given comparator
          var isPresent =  Foosball.Game.get('home_team').find(comparator) !== undefined || 
                           Foosball.Game.get('visitor_team').find(comparator) !== undefined;
                                           
          if(isPresent === true) 
            Foosball.Message.showError('Woops! This player is already logged in!');
                                
          return isPresent;  
        },
        
        // A simple controller that checks if the last user tried to scan a RFID card that 
        // was not found by the system. If no cards where scanned, we assume that the user simply wants to 
        // sign in with his username and password.
        authenticateOptions: function()
        {
            
            if(this.lastRFID)
            {
                var self = this;
                
                // Calls itself with the intention to connect the last RFID number to the user that signs in
                var positive = function(){self.authenticate(true)};
                // Indicates that no connection should be made
                var negative = function(){self.authenticate(false)};
                
                // Displays a option dialog that asks the user if he whishes to connect the new RFID card number to his account. 
                // Note that the option dialog contains a positive button and a negative button to witch we connect callbacks to our functions
                Foosball.Message.showOption('Do you want to conntect your player profile to this RFID?', positive , negative); 
            }
            else
            {
                this.authenticate(false);
            }
        },
      
        // Retrieves username and password values, connects them to the player and 
        // asks for authentication
        authenticate: function(setRFID)
        {            
          // Gets form values
          var username = $('#username').attr('value'); 
          var password = $('#password').attr('value'); 
          
          // Validates content
          if(!username || !password)
          {
            Foosball.Message.showError("Hei! Please provide us with an username and password!");    
            return;
          }
          
          // Checks if a player with the given username is allready authenticated in the game 
          if(this.isPlayerPresent(username) === false)
          {
              
              var self = this,
              options = {
                         success: self.success,
                         error: self.error
                        }
              
               /* 
               Performs an GET request to the server
               based on the options provided. 
                
               If the user is found, and properly authenticated, 
               Then the server will send us a new instance of the user.
               */
               if(setRFID === true)
               {    
                    this.player.set({rfid: this.lastRFID}, {silent:true});
               }
               
               this.player.set({username: username, password: password}, {silent:true});
               this.player.authenticate(options);
            }
        },
        
        // Callback for successful transfers
        success: function()
        {
            this.lastRFID = undefined;
            this.close();
        },
        
        // Callback for faulty transfers
        error: function(model, response)
        {
           model.reset(); 
           Foosball.Message.showError(response.responseText);    
        },
        
        // Handles new user registration
        register: function()
        {
          // Gets form values
          var username = $('#username').attr('value'); 
          var password = $('#password').attr('value'); 
          
          // Validates content
          if(!username || !password)
          {
            Foosball.Message.showError("Hey! Please provide us with an username and password!"); 
            return;
          }

          // Checks if a player with the given username is allready authenticated in the game 
          if(this.isPlayerPresent(username) === false)
          {
                            
              this.player.set({username: username, password: password}, {silent:true});
                  
              var options = 
                  {
                    url: window.serverURL + '/player/register',
                    async: true,
                    success: this.success,
                    error: this.error,
                    wait:true
                  };
               // Saves the player to perform a POST operation to the server   
               this.player.save({}, options);
           }
        },
        
        onClose: function()
        {
            // Sometimes, if the server is slow to respond, then the 
            // typeahead container might still be displayed. If it is present, we simply wish to remove it
            var typeahead = $('.typeahead'); 
            if(typeahead) typeahead.remove();
        }
    }); 
    
    
    /**
     * Handles actions related to starting/stopping a game
     */
    Foosball.GameStateView = Backbone.View.extend
    ({
        el: '#game-state',
        events:
        {
            'click #start-game-button': 'start',
            'click #cancel-game-button': 'cancel'
        },
        
        initialize: function()
        {
           Foosball.Game.on('change:state', this.render, this);
           this.render();
        },
        
        render: function()
        {
           var state =  Foosball.Game.get('state');
           if(state === 'started' || state === 'overtime')
           $(this.el).html('<button id="cancel-game-button" class="button yellow">Abort</button>');    
           
           else
           $(this.el).html('<button id="start-game-button" class="button green">Start!</button>');
        },
        
        start: function()
        {
           Foosball.Game.start();
        },    
        
        cancel: function()
        {
           Foosball.Game.reset();
        }        
    });
    
    /**
     * Binds to the clock model to control and display the match time
     */
    Foosball.ClockView = Backbone.View.extend
    ({
        id: 'clock',
        template: _.template($('#clock-template').html()),
       
        initialize: function()
        {
            Foosball.Clock.on('change:seconds', this.update, this);
        }, 
        
        render: function()
        {
            this.$el.html(this.template()); 
            
            // Stores the refrences to the seconds and minutes containers
            this.seconds = this.$el.find('#seconds');
            this.minutes = this.$el.find('#minutes');
            
            return this;
        },
        
        // Handles rendering of the seconds and minutes, each second. 
        update: function(clock)
        {
            var s = clock.get('seconds'),
            fseconds = this.format(s%60), 
            fminutes = this.format(parseInt(s/60)); 
            
            this.seconds.html(fseconds);
            this.minutes.html(fminutes);
        },
       
       // Formats the values to [xx]
       // Adds an additional zero if the value is < 10
       format: function(value)
       {
            value = value + "";
            if(value.length < 2)
            {
                return "0" + value;
            }
            else
            {
                return value;
            }
       },
       
       onClose:function()
       {
           Foosball.Clock.off('change:seconds', this.update, this);
       }
        
    });
    
    /**
     * Displays the games scoreboard. Binds to the home and visitor score in the game
     */
    Foosball.ScoreBoardView = Backbone.View.extend
    ({
        id: 'score-board',
        template: _.template($('#scoreboard-template').html()),
        events: 
        {
            'click #regret-goal': 'regretGoal'
        },
        
        initialize: function()
        {
            _.bindAll(this, 'render', 'regretGoal', 'update');
             Foosball.Game.on('change:home_score change:visitor_score', this.update, this);
        },
        
        render: function()
        {
            // Renders the template for the scoreboard
            this.$el.html(this.template());
            
            // Renders the clock view inside the scoreboard
            this.clock = new Foosball.ClockView();
            this.$el.find('#clock-container').html(this.clock.render().el);
            
            // Fetches refrences to the home-score and visitor-score containers
            // so that we won't have to look for them every time we wish to update them
            this.homeScore = this.$el.find('#home-score');
            this.visitorScore = this.$el.find('#visitor-score');
            
            return this;
        },
        
        update: function()
        {
            // If a player scores in-game. We only re-render the score label of the team. 
            // Remember that homeScore is a local refrence to the label that is created 
            // when we first render the scoreboard.
            
            this.homeScore.html(Foosball.Game.get('home_score'));
            this.visitorScore.html(Foosball.Game.get('visitor_score'));
        },
        
        regretGoal: function()
        {
             Foosball.Referee.removeGoal();
        },
        
        onClose: function()
        {
           this.clock.close();
            Foosball.Game.off('change:home_score', this.render);  
            Foosball.Game.off('change:visitor_score', this.render);  
        }
    }); 
    
    
    /**
     * Binds to the game-mode buttons on the field. 
     * Changes the game-mode based on user input
     */
    Foosball.GameModeView = Backbone.View.extend
    ({
        el: '#game-mode', 
        template: _.template($('#mode-template').html()),
        events: 
        {
            'click #friendly-mode-button': 'setFriendlyMode',
            'click #ranked-mode-button': 'setRankedMode'
        },
        
        initialize: function()
        {
             Foosball.Game.on('change:state change:mode', this.render, this);
            this.render();
        },
        
        render: function()
        {
            var state =  Foosball.Game.get('state');
            var mode =  Foosball.Game.get('mode');
            
            if(state !== 'new')
            {
                this.$el.hide();
            }
            else
            {
                var attributes = 
                {
                   isFriendlyActive: mode === 'friendly' ? 'button-active yellow' : '',
                   isRankedActive: mode === 'ranked' ? 'button-active yellow' : ''
                }
                
                this.$el.html(this.template(attributes)).show(); 
            }
            return this;
        },
        
        setFriendlyMode: function()
        {
             Foosball.Game.set('mode', 'friendly');
        },
        
        setRankedMode: function()
        {
             Foosball.Game.set('mode', 'ranked');
        }
            
        }); 
    
    /**
     * Renders and displays the game end result. 
     */
    Foosball.ResultView = Backbone.View.extend
    ({
        el: '#result-container',
        template: _.template($('#result-template').html()),
        events:
        {
            'click #finish-button': 'finishGame',
            'click #restart-button': 'restartGame'
        },
        
        initialize: function()
        {
            _.bindAll(this, 'render', 'finishGame', 'restartGame', 'handleGameState');
             Foosball.Game.on('change:state', this.handleGameState);
        },
        
        handleGameState: function()
        {
          var state = Foosball.Game.get('state');  
          if(state === 'end')
          {
              this.render();
          }
          else
          {
              this.$el.hide();
          }
        },
        
        render: function()
        {
           // Picks out the information that we want the 
           // the template to work with. 
           var data = 
                {
                    homeTeam:  Foosball.Game.get('home_team'),
                    visitorTeam:  Foosball.Game.get('visitor_team'),
                    winner:  Foosball.Referee.getMatchLeader()
                };
            
            // Renderns the template
            this.$el.html(this.template(data));
            
            // Centers the element on the screen.
            this.$el.css
            ({
                left: $(window).width() / 2 - this.$el.width() / 2
            });
            
            // The element is at default, hidden, so we fade it slowly in
            this.$el.fadeIn();
            
            return this;
        },
        
        finishGame: function()
        {
             Foosball.Game.finish(); 
        },
        
        restartGame: function()
        {
             Foosball.Game.restart(); 
        }
    }); 
    
    /**
     * Displays the welcome message on the board 
     */
    Foosball.WelcomeView = Backbone.View.extend
    ({
        id: 'welcome',
        template: _.template($('#welcome-template').html()),
        render: function()
        {
           this.$el.html(this.template()); 
           return this;
        }
        
    });
    
    /**
     * Renders the game board onto the field. 
     * Acts as a parent container for other views and handles rendering and destruction of them. 
     */
    Foosball.BoardView = Backbone.View.extend
    ({
        id: 'board-content',
        initialize: function()
        {
           Foosball.Game.on('change:state', this.handleGameState, this);  
          this.showWelcomeMessage();
        },
        
        handleGameState: function()
        {
            var state =  Foosball.Game.get('state');
            if(state === 'new')
            {
                this.showWelcomeMessage();
            }
            else if(state === 'started')
            {
                this.showScoreBoard();
            }
        },
        
        showWelcomeMessage: function()
        {
            var welcomeView = new Foosball.WelcomeView();
            this.show(welcomeView);
        },
        
        showScoreBoard: function()
        {
            var scoreBoard = new Foosball.ScoreBoardView();
            this.show(scoreBoard);
        },
        
        showLoginView: function(player)
        {
          var login = new Foosball.LoginView({player: player});
          this.show(login);
        },
        
        // Helper function to render and display a current view on 
        // to the board. 
        show: function(view)
        { 
            // Checks if a current view is allready rendered and displayed
            if(this.current !== undefined)
            { 
                // Unbinds the events from the current view so
                // that we won't receieve any 'ghosts' callbacks and closes it. 
                this.current.off('close', this.handleGameState, this);   
                this.current.close();
            }
            
            // Binds to this views close event. Sometimes a view might close itself 
            // if an action is successful and the game state changes. We simply make sure that this 
            // board displays a specified view based on the game state if it's old view closed itself. 
            view.on('close', this.handleGameState, this);
            
            this.current = view;
            
            // Switches the view by fading out the current view with the new view. 
            var self = this.$el;
            self.fadeOut('fast', function(){
                self.html(view.render().el);
                self.fadeIn();
            })
        }
    });
    
    /**
     * Handles the display of messages
     */
    Foosball.MessageView = Backbone.View.extend
    ({
        el: '#messages', 
        template: _.template($('#message-template').html()),
        
        events:
        {   
            'click #close-message' : 'hide',
            'click #message-confirm-option' : 'confirm',
            'click #message-decline-option' : 'decline'
        },
        
        // Binds to the 'confirm'-button and 
        // performs the function stored in 
        // 'confirmCallback', given that it is set by showOption
        confirm: function()
        {
          this.confirmCallback(); 
          this.hide();
        },
        
        // Binds to the 'decline'-button and 
        // performs the function stored in 
        // 'declineCallback', given that it is set by showOption
        decline: function()
        {
            this.declineCallback(); 
            this.hide();
        },
        
        // Displays a regular message
        // 'hold' parameter indiates wether the message should 
        // dissapear automaticly after a few seconds or not 
        showMessage: function(message, hold)
        {
            if(!hold)
            hold = false;
        
            var data = 
            {
                message: message,
                type: 'message',
                hold: hold
            };
            this.show(data, hold);
        },
        
        // Displays an error message
        showError: function(message, hold)
        {
            var data = 
            {
                message: message,
                type: 'error',
                hold: hold
            };
            
            this.show(data, hold);
        },
        
        // Displays a option dialog that stores function callbacks for the 
        // 'confirm' and 'decline' buttons 
        showOption: function(message, confirmCallback, declineCallback)
        {
            var hold = true;
            var data = 
            {
                message: message,
                type: 'option',
                hold: hold
            };
            // Stores the refrences to the confirm and decline functions. 
            // These are later called upon by the decline() and confirm() functions 
            this.confirmCallback = confirmCallback;
            this.declineCallback = declineCallback;
            this.show(data, hold);
        },
        
        // Helper method to render and display messages
        show: function(data, hold)
        {
          var self = this;
          self.hide();
          self.$el.html(self.template(data));
          self.$el.slideDown(150);   
          
          // If the hold parameter is not set, then the message will 
          // close and hide itself after 4 seconds. 
            if(!hold)
            {
                self.timer = setTimeout(function() 
                {
                  self.hide();
                }, 4000);
            }
        },
        
        // Helper method to hide the current displayed message
        hide: function()
        {
          var self = this;
          self.$el.slideUp(150);
          if(self.timer)
          {
              clearTimeout(this.timer);
              self.timer = undefined;
          }
        }
    }); 
    
    /**
     * Binds to and renders the teams onto the field
     */
    Foosball.FieldView = Backbone.View.extend
    ({
        el: '#field',
        initialize: function()
        {
           Foosball.Game.on('change:state', this.handleGameState, this);
          this.renderTeams();
        },
        
        renderTeams: function()
        {
           var homeTeamView = new Foosball.TeamView
           ({
                team:  Foosball.Game.get('home_team'),
                el: '#home-team'
           });
           
            
           var visitorTeamView = new Foosball.TeamView
           ({
                team:  Foosball.Game.get('visitor_team'),
                el: '#visitor-team'
           });
        },
        
        handleGameState: function()
        {
            var state = Foosball.Game.get('state');
            if(state === 'new')
            {                
                this.renderTeams();
            }
        }
    });

    /**
     * Main application router. 
     * Kicks of the initialization at page load and sets up the game
     */
    Foosball.ApplicationRouter = Backbone.Router.extend
    ({
        routes: 
        {
            '':   "start"
        },
        
        start: function()
        {
            Foosball.Game = new Foosball.GameModel();
            Foosball.Referee = new Foosball.RefereeModel();
            Foosball.Message = new Foosball.MessageView();
            Foosball.Clock = new Foosball.ClockModel();
    
            Foosball.Board = new Foosball.BoardView();
            $('#board').html(Foosball.Board.render().el);
    
            Foosball.Field = new Foosball.FieldView(); 
    
            Foosball.ModeControl = new Foosball.GameModeView();
            Foosball.StateControl = new Foosball.GameStateView();
            Foosball.Results = new Foosball.ResultView();
        }
    });
    
    Foosball.Application = new Foosball.ApplicationRouter();
    Backbone.history.start();
    

    // Request indicator. 
    // Everytime we make a request to the server, 
    // we want show an indicator until the server responds to us
    $(document).ajaxStart(function()
    { 
        $('#request-indicator').slideDown(250); 
  
    }).ajaxStop(function()
    { 
        $('#request-indicator').stop().fadeOut(150);
    });
 
});


