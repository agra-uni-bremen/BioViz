maxCheckstyleErrors=510

./gradlew core:checkstyleMain > checkstyleOutput 2>&1
wc -l checkstyleOutput > wcResult
read errors filename < wcResult
rm wcResult
echo "found $errors checkstyle errors"
rm ./checkstyleOutput
if [ "$errors" -gt "$maxCheckstyleErrors" ]
then
	echo "Checkstyle FAILED"
	exit 1
else
	echo "Checkstyle SUCCEEDED"
	exit 0
fi

