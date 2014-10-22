/**
 * Created by Christian on 9/3/14.
 */
var originalQuantity;
var arrivedQuantity;
var newQuantity;

    $(document).ready(function() {
        hasChanges=true;
        originalQuantity = document.getElementById("arrivedQuantity").value;
    });

    function calculateNewScopeSupplyQuantity(){
        arrivedQuantity = document.getElementById("arrivedQuantity").value;
        if((parseInt(arrivedQuantity) >= 0) && (parseInt(arrivedQuantity) <= parseInt(originalQuantity))){
            newQuantity  = originalQuantity - arrivedQuantity;
            if(parseInt(newQuantity) >= 0){
                document.getElementById("newQuantitySupplySplit").value = newQuantity;
            }else{
                document.getElementById("arrivedQuantity").value = originalQuantity;
                document.getElementById("newQuantitySupplySplit").value = 0;
            }
        }else{
            document.getElementById("arrivedQuantity").value = "";
            document.getElementById("newQuantitySupplySplit").value = originalQuantity;
        }

    }

    function calculateOldScopeSupplyQuantity(){
        newQuantity = document.getElementById("newQuantitySupplySplit").value;
        if((parseInt(newQuantity) >= 0) && (parseInt(newQuantity) <= parseInt(originalQuantity))){
            arrivedQuantity = originalQuantity - newQuantity;
            if(parseInt(arrivedQuantity) >= 0){
                document.getElementById("arrivedQuantity").value = arrivedQuantity;
            }else{
                document.getElementById("arrivedQuantity").value = originalQuantity;
                document.getElementById("newQuantitySupplySplit").value = 0;
            }
        }else{
            document.getElementById("arrivedQuantity").value = originalQuantity;
            document.getElementById("newQuantitySupplySplit").value = "";
        }
    }

    function verifyArrivedQuantity(){
        arrivedQuantity = document.getElementById("arrivedQuantity").value;
        if(arrivedQuantity == ""){
            document.getElementById("arrivedQuantity").value = 0;
        }
    }

    function verifyNewQuantity(){
        newQuantity = document.getElementById("newQuantitySupplySplit").value;
        if(newQuantity == ""){
            document.getElementById("newQuantitySupplySplit").value = 0;
        }
    }