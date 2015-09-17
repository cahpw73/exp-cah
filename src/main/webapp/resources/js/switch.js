/**
 * Created by Alvaro on 9/3/15.
 */

/*$("[name='my-checkbox']").bootstrapSwitch({
        'state': 'true',
        'size': 'mini'
    }
);*/
create();
function create(){
    $("[name='my-checkbox']").bootstrapSwitch({
            'state': 'true',
            'size': 'mini'
        }
    );
}
function initializeSwitch() {
    liquidated=$.parseJSON($("#editPoForm\\:checkboxLiquidatedDamages").val());
    exchangevariation=$.parseJSON($("#editPoForm\\:checkboxExchangeVariation").val());
    drawingData=$.parseJSON($("#editPoForm\\:checkboxDrawingData").val());
    securityDeposit=$.parseJSON($("#editPoForm\\:checkBoxSecurityDeposit").val());
  /*  console.log("liquidated "+liquidated);
    console.log("exchangevariation "+exchangevariation);
    console.log("drawingdata "+drawingData);
    console.log("securityDeposit "+drawingData);
    console.log("editPoForm:securityDeposit "+securityDeposit);*/
    $("#switchLiquidatedDamages").bootstrapSwitch('state',liquidated);
    $("#switchExchangeVariation").bootstrapSwitch('state',exchangevariation);
    $("#switchDrawingData").bootstrapSwitch('state',drawingData);
    $("#switchSecurityDeposit").bootstrapSwitch('state', securityDeposit);
}

function restartSwitches(){
    create();
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

