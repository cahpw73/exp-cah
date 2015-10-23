/**
 * Created by Alvaro on 9/3/15.
 */
create();
function create(){
    $("[name='my-checkbox']").bootstrapSwitch('destroy', true);
    $("[name='my-checkbox']").bootstrapSwitch({
            'state': 'true',
            'size': 'mini'
        }
    );
}
function initializeSwitch(readOnly) {
    console.log("readonly "+readOnly);
    liquidated=$.parseJSON($("#editPoForm\\:checkboxLiquidatedDamages").val());
    exchangevariation=$.parseJSON($("#editPoForm\\:checkboxExchangeVariation").val());
    drawingData=$.parseJSON($("#editPoForm\\:checkboxDrawingData").val());
    securityDeposit=$.parseJSON($("#editPoForm\\:checkBoxSecurityDeposit").val());
    retention=$.parseJSON($("#editPoForm\\:checkBoxRetention").val());
    $("#switchLiquidatedDamages").bootstrapSwitch('state',liquidated);
    $("#switchExchangeVariation").bootstrapSwitch('state',exchangevariation);
    $("#switchDrawingData").bootstrapSwitch('state',drawingData);
    $("#switchSecurityDeposit").bootstrapSwitch('state', securityDeposit);
    $("#switchRetention").bootstrapSwitch('state', retention);
    if(Boolean(readOnly)) {
        console.log("readonly mode");
        $("#switchLiquidatedDamages").bootstrapSwitch('readonly','true');
        $("#switchExchangeVariation").bootstrapSwitch('readonly','true');
        $("#switchDrawingData").bootstrapSwitch('readonly','true');
        $("#switchSecurityDeposit").bootstrapSwitch('readonly','true');
        $("#switchRetention").bootstrapSwitch('readonly','true');
    }else{
        $('#switchLiquidatedDamages').on('switchChange.bootstrapSwitch', function (e, data) {
            $('#editPoForm\\:checkboxLiquidatedDamages').val(data);
        });
        $('#switchExchangeVariation').on('switchChange.bootstrapSwitch', function (e, data) {
            $('#editPoForm\\:checkboxExchangeVariation').val(data);
        });
        $('#switchDrawingData').on('switchChange.bootstrapSwitch', function (e, data) {
            $('#editPoForm\\:checkboxDrawingData').val(data);
        });
        $('#switchSecurityDeposit').on('switchChange.bootstrapSwitch', function (e, data) {
            $('#editPoForm\\:checkBoxSecurityDeposit').val(data);
            if(data){
                document.getElementById("editPoForm:apply1").checked = true;
                console.log("checked as true");
            }else{
                document.getElementById("editPoForm:apply1").checked = false;
                console.log("checked as false");
            }
            console.log("checkbox value = "+$('#editPoForm\\:apply1').is(':checked'));
        });
        $('#switchRetention').on('switchChange.bootstrapSwitch', function (e, data) {
            $('#editPoForm\\:checkBoxRetention').val(data);
            if(data){
                document.getElementById("editPoForm:apply2").checked = true;
                console.log("checked as true");
            }else{
                document.getElementById("editPoForm:apply2").checked = false;
                console.log("checked as false");
            }
            console.log("checkbox value = "+$('#editPoForm\\:apply2').is(':checked'));
        });
    }
}

function restartSwitches(){
    console.log("restarting switches...");
    create();
    initializeSwitch(false);
}






