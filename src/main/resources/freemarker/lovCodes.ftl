<!DOCTYPE html>
<html lang="en">
<head>
  <title>Bootstrap Example</title>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
  <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
   <script src="\js\lovCodesScripts.js"></script>
</head>
<body>

 <#include "/header.ftl">
 <div class="container-fluid">

    <div class="panel-group">

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

        <div class="panel panel-primary">
            <div class="panel-heading">
                Edit LOV Tables
            </div>
            <div class="panel-body">
                <form class="form-inline"
                       id="frmNewLovEntry"
                       method="Post"
                       action="/lovTables/add">
                    <div class="well">
                        <div class="form-group">
                            <label class="control-label" for="lovList">LOV Type:</label>
                            <select class="form-control" id="lovList" name="lovList">
                                <option value="-1"></option>
                                <#list lovList as lovEntry>
                                    <#if lovEntry == defaultLovCode!"">
                                        <option value="${lovEntry}" selected>${lovEntry}</option>
                                    <#else>
                                        <option value="${lovEntry}">${lovEntry}</option>
                                    </#if>
                                </#list>
                            </select>
                        </div>
                        <div class = "form-group">
                            <label class="control-label" for="newLovEntry">New LOV Entry:</label>
                            <input type="text" id="newLovEntry" name="newLovEntry" value="${newLovEntry!""}">
                             <a href="#" onClick="$(frmNewLovEntry).submit()"">
                                 <span class="glyphicon glyphicon-plus"></span>
                             </a>
                        </div>

                    </div>
                </form>

                <div class="well">
                  <table class="table table-bordered" >
                    <thead>
                      <tr>
                        <th>Description</th>
                        <th>Edit</th>
                        <th>Delete</th>
                      </tr>
                    </thead>
                    <tbody  id="tblLovList">
                    </tbody>
                  </table>
                </div>
            </div>
        </div>
    </div>
 </div>

 <!-- Edit Modal -->
 <div id="editEntryModal" class="modal" role="dialog">
   <div class="modal-dialog modal-md">

     <!-- Modal content-->
     <div class="modal-content">

         <div class="modal-header">
             <button type="button" class="close" data-dismiss="modal">&times;</button>
             <h4 class="modal-title">Edit Description</h4>
         </div>
         <div class="modal-body">
             <form  class="form-horizontal"
                    id="editDescriptionForm"
                    method="Post" action="/lovTables/update">

                 <input type="hidden" id="lovCode" name="lovCode">

                 <div class="form-group">

                    <label class="control-label col-md-3" for="oldDescription">Current Description:</label>
                    <div class="col-md-9">
                        <input type="text" id="originalDescription" name="originalDescription" readOnly>
                    </div>
                 </div>
                 <div class="form-group">

                    <label class="control-label col-md-3" for="newDescription">Edit Description:</label>
                    <div class="col-md-9">
                        <input type="text" id="newDescription" name="newDescription">
                    </div>
                 </div>
                 <button type="submit" class="btn btn-primary">Update</button>

             </form>
         </div>
         <div class="modal-footer">
            <button type="button" class="btn btn-default pull-left" data-dismiss="modal">Cancel</button>
         </div>
     </div>
   </div>
 </div>

 <!-- Delete Modal -->
  <div id="deleteEntryModal" class="modal" role="dialog">
    <div class="modal-dialog modal-md">

      <!-- Modal content-->
      <div class="modal-content">

          <div class="modal-header">
              <button type="button" class="close" data-dismiss="modal">&times;</button>
              <h4 class="modal-title">Confirm Delete</h4>
          </div>
          <div class="modal-body">
              <form
                     id="deleteDescriptionForm"
                     method="Post" action="/lovTables/delete">

                  <input type="hidden" id="delLovCode" name="delLovCode">
                  <input type="hidden" id="delLovDesc" name="delLovDesc">
                  <p id="confirmDeleteLabel">Are you sure you want to delete</p>
                  <button type="submit" class="btn btn-primary">Delete</button>

              </form>
          </div>
          <div class="modal-footer">
             <button type="button" class="btn btn-default pull-left" data-dismiss="modal">Cancel</button>
          </div>
      </div>
    </div>
  </div>

</body>
</html>

