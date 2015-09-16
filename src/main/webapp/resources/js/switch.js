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
    $("#switchLiquidatedDamages").bootstrapSwitch('state',$("#checkboxLiquidatedDamages").val());
    $("#switchExchangeVariation").bootstrapSwitch('state',$("#checkboxExchangeVariation").val());
    $("#switchDrawingData").bootstrapSwitch('state', $("#checkboxDrawingData").val());
    $("#switchSecurityDeposit").bootstrapSwitch('state', $("#checkboxSecurityDeposit").val());
}

function restartSwitches(){
    create();
    initializeSwitch();
}

$('#switchLiquidatedDamages').on('switch-change', function (e, data) {
    var $el = $(data.el)
        , value = data.value;
    console.log(e, $el, value);
});
$('#switchExchangeVariation').on('switch-change', function (e, data) {
    var $el = $(data.el)
        , value = data.value;
    console.log(e, $el, value);
});
$('#switchDrawingData').on('switch-change', function (e, data) {
    var $el = $(data.el)
        , value = data.value;
    console.log(e, $el, value);
});
$('#switchSecurityDeposit').on('switch-change', function (e, data) {
    var $el = $(data.el)
        , value = data.value;
    console.log(e, $el, value);
});
