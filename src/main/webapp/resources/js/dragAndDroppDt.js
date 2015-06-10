/**
 * Created by Christian
 */


function handleDrop(event, ui) {
    console.log("handle drop js")
    var droppedCar = ui.draggable;

    droppedCar.fadeOut('fast');
}

function handleStart(){
    console.log("handle drop start")
}
