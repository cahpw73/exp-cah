/**
 * Created by Alvaro on 9/3/15.
 */
createSwitch();
function createSwitch(){
    $("[name='my-checkbox']").bootstrapSwitch({
            'state': 'true',
            'size': 'mini'
        }
    );
}
function initializeSwitch(readOnly) {
    liquidated=$.parseJSON($('#editPoForm\\:checkboxLiquidatedDamages').val());
    exchangevariation=$.parseJSON($("#editPoForm\\:checkboxExchangeVariation").val());
    drawingData=$.parseJSON($("#editPoForm\\:checkboxDrawingData").val());
    securityDeposit=$.parseJSON($("#editPoForm\\:checkBoxSecurityDeposit").val());
    $("#switchLiquidatedDamages").bootstrapSwitch('state',liquidated);
    $("#switchExchangeVariation").bootstrapSwitch('state',exchangevariation);
    $("#switchDrawingData").bootstrapSwitch('state',drawingData);
    $("#switchSecurityDeposit").bootstrapSwitch('state', securityDeposit);
    if(Boolean(readOnly)) {
        $("#switchLiquidatedDamages").bootstrapSwitch('readonly','true');
        $("#switchExchangeVariation").bootstrapSwitch('readonly','true');
        $("#switchDrawingData").bootstrapSwitch('readonly','true');
        $("#switchSecurityDeposit").bootstrapSwitch('readonly','true');
    }
}

function restartSwitches(){
    createSwitch();
    initializeSwitch();
}

$('#switchLiquidatedDamages').on('switchChange.bootstrapSwitch', function (e, data) {
    $('#checkboxLiquidatedDamages').val(data);
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
});

