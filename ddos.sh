while [ 0 -eq 0 ]
do
    nc 127.0.0.1 5050 &
    echo "ouiiii"
done