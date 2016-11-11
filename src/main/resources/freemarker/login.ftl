<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Reliant LLC</title>
      <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
       <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>

</head>
<body>
    <#if (passcode!"") == "">
        <div class="container-fluid">
            <div class="panel panel-primary">

                <div class="panel-heading">Login</div>
                     <div class="panel-body">
                        <#list errors!"">
                           <div class="panel panel-danger">
                               <div class="panel-heading">The Following Errors Occurred</div>
                                   <div class="panel-body">
                                     <#items as error>
                                         <p>${error.errorMessage}</p>
                                     </#items>
                                   </div>
                               </div>
                           </div>
                        </#list>
                        <div class="well well-sm">
                            <form class="form-inline" method="post" action="/login" id="frmLogin">

                                <div class="row">
                                    <div class="form-group col-md-4">
                                        <label class="control-label" for="passCode">Passcode:</label>
                                        <input  class="form-control"
                                                type = "password"
                                                id="passCode"
                                                name="passCode" maxlength="10" size="10"></input>
                                    </div>
                                    <div class="form-group col-md-3">
                                        <button type="submit" class="btn btn-default btn-md" id="btnLogin">
                                            Login
                                        </button>
                                    </div>
                                </div>
                            </form>
                        </div>
                     </div>
                </div>
            </div>
        </div>
    <#else>
        <a href="/calendar">View Calendar</a>
    </#if>
</body>
</html>