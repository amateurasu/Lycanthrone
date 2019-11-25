(() => {
    var Terminal = (function () {
        var command_line;

        function Terminal(target, PS1, welcome, guide, commands) {
            var instance;
            this.target = target || ".shell .text";
            this.PS1 = PS1 || "$ ";
            this.welcome = welcome || "./hello_friend";
            this.guide = guide || "Run 'help' for basic commands";
            this.commands = commands || ["no commands available"];
            instance = this;
            $(document.body).on('keyup', 'input#command', function (e) {
                var command;
                if (e.keyCode === 13) {
                    $(this).blur();
                    $(this).prop('readonly', true);
                    command = $(this).val();
                    instance.print("<br>");
                    try {
                        return instance["" + command]();
                    } catch (_error) {
                        e = _error;
                        return instance.print("command unavailable");
                    } finally {
                        instance.newline();
                    }
                }
            });
        }

        command_line = '<input type="text" id="command" value="">';
        Terminal.prototype.init = function () {
            return this.greet(this.welcome, 0, 100);
        };

        Terminal.prototype.print = function (element) {
            var $target;
            $target = $(this.target);
            return $target.append(element);
        };

        Terminal.prototype.newline = function () {
            this.print("<br> " + this.PS1);
            this.print(command_line);
            return $("input#command").last().focus();
        };

        Terminal.prototype.greet = function (message, index, interval) {
            if (index < message.length) {
                this.print(message[index++]);
                return setTimeout(((function (_this) {
                    return function () {
                        return _this.greet(message, index, interval);
                    };
                })(this)), interval);
            } else {
                this.print("<br> " + this.guide);
                return this.newline();
            }
        };

        Terminal.prototype.help = function () {
            var command;
            let ref = this.commands;
            let results = [];
            for (let i = 0, len = ref.length; i < len; i++) {
                command = ref[i];
                results.push(this.print(command + "<br>"));
            }
            return results;
        };
        return Terminal;
    })();
    var terminal = new Terminal();
    terminal.init();
})();