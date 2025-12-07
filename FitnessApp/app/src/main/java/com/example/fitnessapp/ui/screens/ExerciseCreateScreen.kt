    package com.example.fitnessapp.ui.screens

    import android.net.Uri
    import androidx.activity.compose.rememberLauncherForActivityResult
    import androidx.activity.result.contract.ActivityResultContracts
    import androidx.compose.foundation.background
    import androidx.compose.foundation.border
    import androidx.compose.foundation.clickable
    import androidx.compose.foundation.interaction.MutableInteractionSource
    import androidx.compose.foundation.layout.Box
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.Spacer
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.height
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.rememberScrollState
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.foundation.verticalScroll
    import androidx.compose.material3.DropdownMenuItem
    import androidx.compose.material3.ExperimentalMaterial3Api
    import androidx.compose.material3.ExposedDropdownMenuBox
    import androidx.compose.material3.ExposedDropdownMenuDefaults
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.material3.Text
    import androidx.compose.material3.TextField
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.getValue
    import androidx.compose.runtime.mutableStateOf
    import androidx.compose.runtime.remember
    import androidx.compose.runtime.setValue
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.draw.clip
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import androidx.navigation.NavController
    import coil.compose.AsyncImage
    import com.example.fitnessapp.ui.viewmodel.ExerciseViewModel

    @Composable
    fun ExerciseCreateScreen(
        navController: NavController,
        viewModel: ExerciseViewModel
    ) {
        var name by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var muscleGroup by remember { mutableStateOf("Грудь") }
        var difficulty by remember { mutableStateOf("новичок") }
        var equipment by remember { mutableStateOf("") }

        var previewImageUri by remember { mutableStateOf<Uri?>(null) }
        var tutorialImageUri by remember { mutableStateOf<Uri?>(null) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                "Добавить упражнение",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(16.dp))

            // 🔹 Превью-картинка
            ImagePicker(
                label = "Добавить превью",
                selectedImage = previewImageUri,
                onImageSelected = { previewImageUri = it }
            )

            Spacer(Modifier.height(16.dp))

            // 🔹 GIF/ Tutorial
            ImagePicker(
                label = "Добавить GIF / Tutorial",
                selectedImage = tutorialImageUri,
                onImageSelected = { tutorialImageUri = it }
            )


            Spacer(Modifier.height(16.dp))

            InputField("Название", name) { name = it }
            InputField("Описание", description) { description = it }
            MuscleGroupDropdown(
                selected = muscleGroup,
                onSelect = { muscleGroup = it }
            )
            Spacer(Modifier.height(16.dp))
            DifficultyDropdown(
                selected = difficulty,
                onSelect = { difficulty = it.lowercase() }
            )
            InputField("Оборудование", equipment) { equipment = it }

            Spacer(Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        viewModel.createExercise(
                            name,
                            description,
                            muscleGroup,
                            difficulty,
                            equipment,
                            previewImageUri,
                            tutorialImageUri
                        )
                        navController.popBackStack()
                    }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Создать",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    @Composable
    fun ImagePicker(
        label: String,
        selectedImage: Uri?,
        onImageSelected: (Uri?) -> Unit
    ) {

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->
            onImageSelected(uri)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF2E2E2E))
                .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ){ launcher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (selectedImage != null) {
                AsyncImage(
                    model = selectedImage,
                    contentDescription = label,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(label, color = Color.Gray)
            }
        }
    }

    @Composable
    fun InputField(title: String, value: String, onChange: (String) -> Unit) {
        Column(Modifier.fillMaxWidth()) {
            Text(title, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
            TextField(
                value = value,
                onValueChange = onChange,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(Modifier.height(12.dp))
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MuscleGroupDropdown(selected: String, onSelect: (String) -> Unit) {
        val categories = listOf(
            "Спина", "Грудь", "Ноги",
            "Плечи", "Бицепс", "Трицепс", "Пресс"
        )

        var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = selected,
                onValueChange = {},
                readOnly = true,
                label = { Text("Группа мышц") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { group ->
                    DropdownMenuItem(
                        text = { Text(group) },
                        onClick = {
                            onSelect(group)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DifficultyDropdown(selected: String, onSelect: (String) -> Unit) {
        val levels = listOf("Новичок", "Средний", "Продвинутый")

        var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = selected,
                onValueChange = {},
                readOnly = true,
                label = { Text("Сложность") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                levels.forEach { lvl ->
                    DropdownMenuItem(
                        text = { Text(lvl) },
                        onClick = {
                            onSelect(lvl)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
