var session = getCookie('JSESSION');
console.log(session);
if (session.length > 0) {
    var user = localStorage.getItem('us');
    console.log(user);
    if(user==null)user='User';
    $("#info").text(user);
    $('#lf').css('display', 'none');
    $('#lt').css('display', 'flex');
} else {
    $('#lt').css('display', 'none');
    $('#lf').css('display', 'flex');
}

function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}

$(document).ready(function () {
    $('#logout').click(function(){
        setCookie("JSESSION", "", 1);
        localStorage.clear();
        location.reload();
    });
});

function setCookie(cname,cvalue,exdays) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays*24*60*60*1000));
    var expires = "expires=" + d.toGMTString();
    document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}


$("#book").click(function () {
    var radioValue = $("input[name='check']:checked").val();
    var session = getCookie('JSESSION');

    console.log(session);
    var event = $('#event').val();
    if (session.length < 10 ) {
        document.location.href = '/login';
    } else {
        var body = {
            pay:radioValue,
            session: session,
            event: event
        };
        $.ajax({
            type: "POST",
            url: "/book",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(body),
            success: function (resp) {
                var data = (resp);
                if (data.status == 1) {
                    alert(data.message);
                } else document.location.href = '/login';
            },
            error: function (resp) {
                console.log(resp);
            }
        });
    }

})
;

$("#login").click(function(){
    var phone = $('#phone').val();
    var password = $('#password').val();
    var body = {
        phone: phone,
        password: password
    };
    $.ajax({
        type: "POST",
        url: "/login",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(body),
        success: function (resp) {
            var data = JSON.parse(resp);
            if (data.status == 1){
                localStorage.setItem("us", phone);
                document.location.href = '/home';
            } else alert('Login false')
        },
        error: function (resp) {
            console.log(resp);
        }
    });
});

$("#pay").click(function () {
    var session = getCookie('JSESSION');
    var code = $('#code').val();

    console.log(code)
    if (session.length < 10) {
        document.location.href = '/login';
    } else {
        var body = {
            session: session,
            code: code
        };
        $.ajax({
            type: "POST",
            url: "/pay",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(body),
            success: function (resp) {
                var data = resp;
                alert(data.message);
            },
            error: function (resp) {
                console.log(resp);
            }
        });
    }
});


$("#register").click(function () {
    var phone = $('#phone').val();
    var lastName = $('#lastName').val();
    var firstName = $('#firstName').val();
    var address = $('#address').val();
    var password = $('#password').val();
    var repassword = $('#repass').val();



    if (password!=repassword) {
        alert("sai mat khau")
    } else {
        var body = {
            phone:phone,
            lastName: lastName,
            firstName: firstName,
            address: address,
            password: password

        };
        $.ajax({
            type: "POST",
            url: "/register",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(body),
            success: function (resp) {
                var data = (resp);
                if (data.status == 1) {
                    localStorage.setItem("us", phone);
                    document.location.href = '/home';
                } else alert(data.message);
            },
            error: function (resp) {
                console.log(resp);
            }
        });
    }

})
;






