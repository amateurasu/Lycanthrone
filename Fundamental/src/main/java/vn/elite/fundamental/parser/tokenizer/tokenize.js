function Token(type, value) {
    this.type = type;
    this.value = value;
}

function isDigit(ch) {
    return /\d/.test(ch);
}

function isLetter(ch) {
    return /[a-z]/i.test(ch);
}

function isOperator(ch) {
    return /[+\-*\/^]/.test(ch);
}

function isLeftParenthesis(ch) {
    return /\(/.test(ch);
}

function isRightParenthesis(ch) {
    return /\)/.test(ch);
}

function tokenize(str) {
    const result = [];
    let letterBuffer = [];
    let numberBuffer = [];

    const emptyLetterBufferAsVariables = () => {
        const l = letterBuffer.length;
        for (let i = 0; i < l; i++) {
            result.push(new Token("Variable", letterBuffer[i]));
            if (i < l - 1) { //there are more Variables left
                result.push(new Token("Operator", "*"));
            }
        }
        letterBuffer = [];
    };

    const emptyNumberBufferAsLiteral = () => {
        result.push(new Token("Literal", numberBuffer.join("")));
        numberBuffer = [];
    };

    str.replace(/\s+/g, "").split("").forEach(char => {
        if (isDigit(char)) {
            numberBuffer.push(char);
        } else if (char === ".") {
            numberBuffer.push(char);
        } else if (isLetter(char)) {
            if (numberBuffer.length) {
                emptyNumberBufferAsLiteral();
                result.push(new Token("Operator", "*"));
            }
            letterBuffer.push(char);
        } else if (isOperator(char)) {
            if (numberBuffer.length) {
                emptyNumberBufferAsLiteral();
            }
            if (letterBuffer.length) {
                emptyLetterBufferAsVariables();
            }
            result.push(new Token("Operator", char));
        } else if (isLeftParenthesis(char)) {
            if (letterBuffer.length) {
                result.push(new Token("Function", letterBuffer.join("")));
                letterBuffer = [];
            } else if (numberBuffer.length) {
                emptyNumberBufferAsLiteral();
                result.push(new Token("Operator", "*"));
            }
            result.push(new Token("Left Parenthesis", char));
        } else if (isRightParenthesis(char)) {
            if (letterBuffer.length) {
                emptyLetterBufferAsVariables();
            } else if (numberBuffer.length) {
                emptyNumberBufferAsLiteral();
            }
            result.push(new Token("Right Parenthesis", char));
        }
    });
    if (numberBuffer.length) {
        emptyNumberBufferAsLiteral();
    }
    if (letterBuffer.length) {
        emptyLetterBufferAsVariables();
    }
    console.log(str);
    return result;
}

const s = "8.0.9sin(45) + 2.2x/7 - 2^6";
const tokens = tokenize(s);
tokens.forEach((token, index) => {
    console.log(index + " => " + token.type + "(" + token.value + ")");
});
eval(s);
